package parser;

import parser.ast.BinaryOpNode;
import parser.ast.ExpressionNode;
import parser.ast.IdentifierNode;
import parser.ast.LambdaDeclarationNode;
import parser.ast.NumberLiteralNode;
import parser.ast.ParameterNode;
import parser.ast.ProgramNode;
import scanner.Scanner;

import java.util.ArrayList;
import java.util.List;

public final class LambdaParser {

    private LambdaParser() {
    }

    public static ParseResult parse(List<Scanner.Lexeme> lexemes) {
        ParserState parserState = new ParserState(lexemes);
        ProgramNode ast = parserState.parseProgram();
        return new ParseResult(ast, parserState.errors);
    }

    public static class ParseResult {
        private final ProgramNode ast;
        private final List<Scanner.ErrorInfo> errors;

        public ParseResult(ProgramNode ast, List<Scanner.ErrorInfo> errors) {
            this.ast = ast;
            this.errors = errors == null ? List.of() : List.copyOf(errors);
        }

        public ProgramNode getAst() {
            return ast;
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
            if (lexemes == null || lexemes.isEmpty()) {
                this.lexemes = List.of(new Scanner.Lexeme(Scanner.TokenKind.EOF, "<EOF>", 1, 1));
            } else {
                this.lexemes = lexemes;
            }
        }

        private ProgramNode parseProgram() {
            List<LambdaDeclarationNode> declarations = new ArrayList<>();

            while (!isAtEnd()) {
                LambdaDeclarationNode declaration = parseStatement();
                if (declaration != null) {
                    declarations.add(declaration);
                }
            }

            return new ProgramNode(declarations);
        }

        private LambdaDeclarationNode parseStatement() {
            int startIndex = currentIndex;

            Scanner.Lexeme nameToken = consume(
                    Scanner.TokenKind.IDENTIFIER,
                    "Ожидался идентификатор слева от '='."
            );
            consume(Scanner.TokenKind.ASSIGN, "Ожидался знак '=' после имени.");
            consume(Scanner.TokenKind.LAMBDA, "Ожидалось ключевое слово 'lambda'.");

            List<ParameterNode> parameters = parseParameters();

            consume(Scanner.TokenKind.COLON, "Ожидалось двоеточие ':' после параметров.");
            ExpressionNode expression = parseExpression();

            boolean hasSemicolon = match(Scanner.TokenKind.SEMICOLON);
            if (!hasSemicolon) {
                reportError(current(), "Ожидалась точка с запятой ';' в конце выражения.");
                recoverToNextStatement();
            }

            if (nameToken == null || expression == null) {
                if (currentIndex == startIndex) {
                    advance();
                }
                return null;
            }

            return new LambdaDeclarationNode(
                    nameToken.getLexeme(),
                    nameToken.getLine(),
                    nameToken.getColumn(),
                    parameters,
                    expression
            );
        }

        private List<ParameterNode> parseParameters() {
            List<ParameterNode> parameters = new ArrayList<>();
            if (check(Scanner.TokenKind.COLON)) {
                return parameters;
            }

            Scanner.Lexeme first = consume(
                    Scanner.TokenKind.IDENTIFIER,
                    "Ожидался идентификатор параметра."
            );
            if (first != null) {
                parameters.add(new ParameterNode(first.getLexeme(), first.getLine(), first.getColumn()));
            }

            while (match(Scanner.TokenKind.COMMA)) {
                Scanner.Lexeme nextParam = consume(
                        Scanner.TokenKind.IDENTIFIER,
                        "Ожидался идентификатор параметра после ','."
                );
                if (nextParam != null) {
                    parameters.add(new ParameterNode(nextParam.getLexeme(), nextParam.getLine(), nextParam.getColumn()));
                }
            }

            while (check(Scanner.TokenKind.IDENTIFIER)) {
                reportError(current(), "Ожидалась запятая между параметрами.");
                Scanner.Lexeme missingCommaParam = advance();
                parameters.add(new ParameterNode(
                        missingCommaParam.getLexeme(),
                        missingCommaParam.getLine(),
                        missingCommaParam.getColumn()
                ));
            }

            return parameters;
        }

        private ExpressionNode parseExpression() {
            ExpressionNode expression = parseTerm();

            while (check(Scanner.TokenKind.PLUS) || check(Scanner.TokenKind.MINUS)) {
                Scanner.Lexeme operator = advance();
                ExpressionNode right = parseTerm();
                if (right == null || expression == null) {
                    continue;
                }
                expression = new BinaryOpNode(
                        operator.getLexeme(),
                        operator.getLine(),
                        operator.getColumn(),
                        expression,
                        right
                );
            }

            return expression;
        }

        private ExpressionNode parseTerm() {
            ExpressionNode term = parseFactor();

            while (check(Scanner.TokenKind.STAR) || check(Scanner.TokenKind.SLASH)) {
                Scanner.Lexeme operator = advance();
                ExpressionNode right = parseFactor();
                if (right == null || term == null) {
                    continue;
                }
                term = new BinaryOpNode(
                        operator.getLexeme(),
                        operator.getLine(),
                        operator.getColumn(),
                        term,
                        right
                );
            }

            return term;
        }

        private ExpressionNode parseFactor() {
            if (match(Scanner.TokenKind.IDENTIFIER)) {
                Scanner.Lexeme token = previous();
                return new IdentifierNode(token.getLexeme(), token.getLine(), token.getColumn());
            }

            if (match(Scanner.TokenKind.NUMBER)) {
                Scanner.Lexeme token = previous();
                return new NumberLiteralNode(token.getLexeme(), token.getLine(), token.getColumn());
            }

            if (match(Scanner.TokenKind.LPAREN)) {
                ExpressionNode nested = parseExpression();
                consume(Scanner.TokenKind.RPAREN, "Ожидалась закрывающая скобка ')'.");
                return nested;
            }

            reportError(current(), "Ожидался идентификатор, число или выражение в скобках.");
            if (!isAtEnd()) {
                advance();
            }
            return null;
        }

        private Scanner.Lexeme consume(Scanner.TokenKind expected, String message) {
            if (match(expected)) {
                return previous();
            }
            reportError(current(), message);
            if (!isAtEnd()) {
                advance();
            }
            return null;
        }

        private void recoverToNextStatement() {
            while (!isAtEnd() && !check(Scanner.TokenKind.SEMICOLON) && !check(Scanner.TokenKind.IDENTIFIER)) {
                advance();
            }

            if (check(Scanner.TokenKind.SEMICOLON)) {
                advance();
            }
        }

        private void reportError(Scanner.Lexeme token, String message) {
            String fragment = token.isEof() ? "<конец ввода>" : token.getLexeme();
            int length = token.isEof() ? 0 : token.getLength();

            errors.add(new Scanner.ErrorInfo(
                    "Синтаксическая ошибка",
                    fragment,
                    message,
                    token.getLine(),
                    token.getColumn(),
                    length
            ));
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

        private Scanner.Lexeme previous() {
            int index = Math.max(currentIndex - 1, 0);
            return lexemes.get(index);
        }

        private Scanner.Lexeme current() {
            return lexemes.get(Math.min(currentIndex, lexemes.size() - 1));
        }

        private boolean isAtEnd() {
            return current().getKind() == Scanner.TokenKind.EOF;
        }
    }
}
