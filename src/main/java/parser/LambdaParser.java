package parser;

import scanner.Scanner;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LambdaParser {

    public static final String RECURSIVE_DESCENT_GRAMMAR = """
            <program> -> <statement> <program-tail>
            <program-tail> -> <statement> <program-tail> | e
            <statement> -> <identifier> '=' <lambda-expression> ';'
            <identifier> -> letter <identifier-tail>
            <identifier-tail> -> letter | digit | '_' <identifier-tail> | e
            <lambda-expression> -> 'lambda' <parameters> ':' <expression>
            <parameters> -> e | <identifier> <parameters-tail>
            <parameters-tail> -> ',' <identifier> <parameters-tail> | e
            <expression> -> <term> <expression-tail>
            <expression-tail> -> '+' <term> <expression-tail> | '-' <term> <expression-tail> | e
            <term> -> <factor> <term-tail>
            <term-tail> -> '*' <factor> <term-tail> | '/' <factor> <term-tail> | e
            <factor> -> '(' <expression> ')' | <identifier> | <number>
            """;

    private static final EnumSet<Scanner.TokenKind> STATEMENT_FOLLOW =
            EnumSet.of(Scanner.TokenKind.EOF);
    private static final EnumSet<Scanner.TokenKind> PARAMETERS_FOLLOW =
            EnumSet.of(Scanner.TokenKind.COLON, Scanner.TokenKind.SEMICOLON, Scanner.TokenKind.EOF);
    private static final EnumSet<Scanner.TokenKind> EXPRESSION_FOLLOW =
            EnumSet.of(Scanner.TokenKind.RPAREN, Scanner.TokenKind.SEMICOLON, Scanner.TokenKind.EOF);
    private static final EnumSet<Scanner.TokenKind> TERM_FOLLOW =
            EnumSet.of(
                    Scanner.TokenKind.PLUS,
                    Scanner.TokenKind.MINUS,
                    Scanner.TokenKind.RPAREN,
                    Scanner.TokenKind.SEMICOLON,
                    Scanner.TokenKind.EOF
            );
    private static final EnumSet<Scanner.TokenKind> FACTOR_START =
            EnumSet.of(Scanner.TokenKind.IDENTIFIER, Scanner.TokenKind.NUMBER, Scanner.TokenKind.LPAREN);

    public static ParseResult parse(List<Scanner.Lexeme> lexemes) {
        ParserState parser = new ParserState(lexemes);
        parser.parseProgram();
        return new ParseResult(parser.errors);
    }

    public static class ParseResult {
        private final List<Scanner.ErrorInfo> errors;

        public ParseResult(List<Scanner.ErrorInfo> errors) {
            this.errors = errors;
        }

        public boolean isSuccess() {
            return errors.isEmpty();
        }

        public List<Scanner.ErrorInfo> getErrors() {
            return errors;
        }
    }

    private static class ParserState {
        private final List<Scanner.Lexeme> lexemes;
        private final List<Scanner.ErrorInfo> errors = new ArrayList<>();
        private int currentIndex = 0;

        private ParserState(List<Scanner.Lexeme> lexemes) {
            this.lexemes = lexemes == null ? List.of() : lexemes;
        }

        private void parseProgram() {
            if (isAtEnd()) {
                return;
            }
            
            parseStatement();
            
            while (!isAtEnd() && current().getKind() == Scanner.TokenKind.IDENTIFIER) {
                parseStatement();
            }
            
            reportTrailingTokens();
        }

        private void parseStatement() {
            consume(
                    Scanner.TokenKind.IDENTIFIER,
                    "идентификатор слева от '='",
                    EnumSet.of(Scanner.TokenKind.ASSIGN, Scanner.TokenKind.LAMBDA),
                    STATEMENT_FOLLOW
            );

            consume(
                    Scanner.TokenKind.ASSIGN,
                    "знак '=' после имени функции",
                    EnumSet.of(Scanner.TokenKind.LAMBDA, Scanner.TokenKind.IDENTIFIER, Scanner.TokenKind.COLON),
                    STATEMENT_FOLLOW
            );

            parseLambdaExpression();

            consume(
                    Scanner.TokenKind.SEMICOLON,
                    "точка с запятой ';' в конце выражения",
                    EnumSet.noneOf(Scanner.TokenKind.class),
                    STATEMENT_FOLLOW
            );
        }

        private void parseLambdaExpression() {
            consume(
                    Scanner.TokenKind.LAMBDA,
                    "ключевое слово 'lambda'",
                    EnumSet.of(Scanner.TokenKind.IDENTIFIER, Scanner.TokenKind.COLON),
                    STATEMENT_FOLLOW
            );

            parseParameters();

            consume(
                    Scanner.TokenKind.COLON,
                    "двоеточие ':' после списка параметров",
                    FACTOR_START,
                    STATEMENT_FOLLOW
            );

            parseExpression();
        }

        private void parseParameters() {
            if (check(Scanner.TokenKind.COLON)) {
                return;
            }

            consume(
                    Scanner.TokenKind.IDENTIFIER,
                    "идентификатор параметра",
                    EnumSet.of(Scanner.TokenKind.COMMA, Scanner.TokenKind.COLON),
                    PARAMETERS_FOLLOW
            );

            parseParametersTail();
        }

        private void parseParametersTail() {
            if (match(Scanner.TokenKind.COMMA)) {
                consume(
                        Scanner.TokenKind.IDENTIFIER,
                        "идентификатор параметра после ','",
                        EnumSet.of(Scanner.TokenKind.COMMA, Scanner.TokenKind.COLON),
                        PARAMETERS_FOLLOW
                );
                parseParametersTail();
                return;
            }

            if (check(Scanner.TokenKind.IDENTIFIER)) {
                reportCurrent(current(), "Ожидалась ',' между параметрами.");
                advance();
                parseParametersTail();
                return;
            }

            if (!PARAMETERS_FOLLOW.contains(current().getKind())) {
                reportCurrent(current(), "Ожидалась ',' или ':' после параметра.");
                synchronize(PARAMETERS_FOLLOW);
            }
        }

        private void parseExpression() {
            parseTerm();
            parseExpressionTail();
        }

        private void parseExpressionTail() {
            if (match(Scanner.TokenKind.PLUS) || match(Scanner.TokenKind.MINUS)) {
                parseTerm();
                parseExpressionTail();
                return;
            }

            if (!EXPRESSION_FOLLOW.contains(current().getKind())) {
                reportCurrent(current(), "Ожидался оператор '+', '-', или завершение выражения.");
                synchronize(EXPRESSION_FOLLOW);
            }
        }

        private void parseTerm() {
            parseFactor();
            parseTermTail();
        }

        private void parseTermTail() {
            if (match(Scanner.TokenKind.STAR) || match(Scanner.TokenKind.SLASH)) {
                parseFactor();
                parseTermTail();
                return;
            }

            if (!TERM_FOLLOW.contains(current().getKind())) {
                reportCurrent(current(), "Ожидался оператор '*', '/', или завершение подвыражения.");
                synchronize(union(TERM_FOLLOW, EXPRESSION_FOLLOW));
            }
        }

        private void parseFactor() {
            if (match(Scanner.TokenKind.IDENTIFIER) || match(Scanner.TokenKind.NUMBER)) {
                return;
            }

            if (match(Scanner.TokenKind.LPAREN)) {
                parseExpression();
                consume(
                        Scanner.TokenKind.RPAREN,
                        "закрывающая скобка ')'",
                        TERM_FOLLOW,
                        EXPRESSION_FOLLOW
                );
                return;
            }

            if (EnumSet.of(
                    Scanner.TokenKind.PLUS,
                    Scanner.TokenKind.MINUS,
                    Scanner.TokenKind.STAR,
                    Scanner.TokenKind.SLASH,
                    Scanner.TokenKind.RPAREN,
                    Scanner.TokenKind.SEMICOLON,
                    Scanner.TokenKind.EOF
            ).contains(current().getKind())) {
                reportCurrent(current(), "Ожидался идентификатор, число, или подвыражение в скобках.");
                return;
            }

            reportCurrent(current(), "Недопустимый фрагмент в выражении. Ожидался идентификатор, число, или '('.");
            synchronize(union(FACTOR_START, EXPRESSION_FOLLOW, TERM_FOLLOW));
            if (FACTOR_START.contains(current().getKind())) {
                parseFactor();
            }
        }

        private void reportTrailingTokens() {
            while (!isAtEnd()) {
                reportCurrent(current(), "Лишний фрагмент после завершения конструкции.");
                advance();
            }
        }

        private Scanner.Lexeme consume(
                Scanner.TokenKind expected,
                String expectedDescription,
                EnumSet<Scanner.TokenKind> insertionFollowers,
                EnumSet<Scanner.TokenKind> syncSet
        ) {
            if (match(expected)) {
                return previous();
            }

            Scanner.Lexeme token = current();

            if (insertionFollowers.contains(token.getKind()) || syncSet.contains(token.getKind())) {
                reportMissing(token, expectedDescription);
                return null;
            }

            reportCurrent(token, "Ожидался " + expectedDescription + ".");
            synchronize(union(insertionFollowers, syncSet, EnumSet.of(expected)));

            if (match(expected)) {
                return previous();
            }

            if (insertionFollowers.contains(current().getKind()) || syncSet.contains(current().getKind())) {
                reportMissing(current(), expectedDescription);
            }
            return null;
        }

        private void reportCurrent(Scanner.Lexeme token, String description) {
            String fragment = token.isEof() ? "<конец ввода>" : token.getLexeme();
            int length = token.isEof() ? 0 : token.getLength();
            errors.add(new Scanner.ErrorInfo(
                    "Синтаксическая ошибка",
                    fragment,
                    description + " Найдено '" + fragment + "'.",
                    token.getLine(),
                    token.getColumn(),
                    length
            ));
        }

        private void reportMissing(Scanner.Lexeme token, String expectedDescription) {
            String fragment = token.isEof() ? "<конец ввода>" : token.getLexeme();
            errors.add(new Scanner.ErrorInfo(
                    "Синтаксическая ошибка",
                    fragment,
                    "Ожидался " + expectedDescription + ".",
                    token.getLine(),
                    token.getColumn(),
                    0
            ));
        }

        private void synchronize(EnumSet<Scanner.TokenKind> syncTokens) {
            while (!isAtEnd() && !syncTokens.contains(current().getKind())) {
                advance();
            }
        }

        private EnumSet<Scanner.TokenKind> union(EnumSet<Scanner.TokenKind> first, EnumSet<Scanner.TokenKind> second) {
            EnumSet<Scanner.TokenKind> result = EnumSet.noneOf(Scanner.TokenKind.class);
            result.addAll(first);
            result.addAll(second);
            return result;
        }

        private EnumSet<Scanner.TokenKind> union(
                EnumSet<Scanner.TokenKind> first,
                EnumSet<Scanner.TokenKind> second,
                EnumSet<Scanner.TokenKind> third
        ) {
            EnumSet<Scanner.TokenKind> result = EnumSet.noneOf(Scanner.TokenKind.class);
            result.addAll(first);
            result.addAll(second);
            result.addAll(third);
            return result;
        }

        private boolean match(Scanner.TokenKind expected) {
            if (!check(expected)) {
                return false;
            }
            advance();
            return true;
        }

        private boolean check(Scanner.TokenKind expected) {
            return current().getKind() == expected;
        }

        private Scanner.Lexeme advance() {
            if (!isAtEnd()) {
                currentIndex++;
            }
            return previous();
        }

        private boolean isAtEnd() {
            return current().getKind() == Scanner.TokenKind.EOF;
        }

        private Scanner.Lexeme current() {
            if (lexemes.isEmpty()) {
                return new Scanner.Lexeme(Scanner.TokenKind.EOF, "<EOF>", 1, 1);
            }
            return lexemes.get(Math.min(currentIndex, lexemes.size() - 1));
        }

        private Scanner.Lexeme previous() {
            int index = Math.max(currentIndex - 1, 0);
            return lexemes.get(index);
        }
    }
}
