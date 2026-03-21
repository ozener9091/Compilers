package scanner;

import antlr.LambdaLexer;
import antlr.LambdaParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class AntlrAdapter {

    public static class AntlrResult {
        private final List<Scanner.TokenInfo> tokens;
        private final List<Scanner.ErrorInfo> lexicalErrors;
        private final List<Scanner.ErrorInfo> syntaxErrors;

        public AntlrResult(List<Scanner.TokenInfo> tokens, List<Scanner.ErrorInfo> lexicalErrors, List<Scanner.ErrorInfo> syntaxErrors) {
            this.tokens = tokens;
            this.lexicalErrors = lexicalErrors;
            this.syntaxErrors = syntaxErrors;
        }

        public List<Scanner.TokenInfo> getTokens() {
            return tokens;
        }

        public List<Scanner.ErrorInfo> getLexicalErrors() {
            return lexicalErrors;
        }

        public List<Scanner.ErrorInfo> getSyntaxErrors() {
            return syntaxErrors;
        }

        public List<Scanner.ErrorInfo> getAllErrors() {
            List<Scanner.ErrorInfo> allErrors = new ArrayList<>();
            allErrors.addAll(lexicalErrors);
            allErrors.addAll(syntaxErrors);
            return allErrors;
        }
    }

    public static class AntlrLexicalErrorListener implements ANTLRErrorListener {
        private final List<Scanner.ErrorInfo> errors = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg, RecognitionException e) {
            String fragment = extractFragment(offendingSymbol);
            String errorType = "Лексическая ошибка";
            String description = msg;

            if (offendingSymbol instanceof Token) {
                Token token = (Token) offendingSymbol;
                if (token.getType() == LambdaLexer.UNKNOWN) {
                    fragment = token.getText();
                    description = "Недопустимый символ '" + fragment + "'.";
                }
            }

            errors.add(new Scanner.ErrorInfo(
                    errorType,
                    fragment,
                    description,
                    line,
                    charPositionInLine + 1,
                    fragment.length()
            ));
        }

        @Override
        public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                    boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                                BitSet conflictingAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                             int prediction, ATNConfigSet configs) {
        }

        private String extractFragment(Object offendingSymbol) {
            if (offendingSymbol == null) {
                return "<конец ввода>";
            }
            if (offendingSymbol instanceof Token) {
                Token token = (Token) offendingSymbol;
                String text = token.getText();
                if (text == null || text.isEmpty()) {
                    return "<конец ввода>";
                }
                return text;
            }
            if (offendingSymbol instanceof String) {
                return (String) offendingSymbol;
            }
            return offendingSymbol.toString();
        }

        public List<Scanner.ErrorInfo> getErrors() {
            return errors;
        }
    }

    public static AntlrResult analyze(String input) {
        List<Scanner.TokenInfo> tokens = new ArrayList<>();
        List<Scanner.ErrorInfo> lexicalErrors = new ArrayList<>();
        List<Scanner.ErrorInfo> syntaxErrors = new ArrayList<>();

        CharStream charStream = CharStreams.fromString(input);
        LambdaLexer lexer = new LambdaLexer(charStream);

        AntlrLexicalErrorListener lexicalErrorListener = new AntlrLexicalErrorListener();
        lexer.removeErrorListeners();
        lexer.addErrorListener(lexicalErrorListener);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LambdaParser parser = new LambdaParser(tokenStream);

        // Используем стратегию восстановления по умолчанию
        parser.setErrorHandler(new DefaultErrorStrategy());

        // Создаем listener для сбора синтаксических ошибок
        AntlrErrorListener errorListener = new AntlrErrorListener();

        try {
            LambdaParser.ProgramContext context = parser.program();

            // Проходим по дереву разбора и собираем ошибки через listener
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(errorListener, context);

            for (Token token : tokenStream.getTokens()) {
                if (token.getType() == LambdaLexer.EOF) {
                    continue;
                }

                String tokenCode = getTokenCode(token.getType());
                String tokenType = getTokenTypeName(token.getType());
                String tokenText = token.getText();
                String location = (token.getLine()) + ":" + (token.getCharPositionInLine() + 1);

                tokens.add(new Scanner.TokenInfo(tokenCode, tokenType, tokenText, location));
            }

        } catch (ParseCancellationException ex) {
            syntaxErrors.add(new Scanner.ErrorInfo(
                    "Синтаксическая ошибка",
                    "<конец ввода>",
                    "Преждевременное завершение разбора: " + ex.getMessage(),
                    1,
                    1,
                    0
            ));
        }

        lexicalErrors.addAll(lexicalErrorListener.getErrors());
        syntaxErrors.addAll(errorListener.getErrors());

        return new AntlrResult(tokens, lexicalErrors, syntaxErrors);
    }

    private static String getTokenCode(int tokenType) {
        return switch (tokenType) {
            case LambdaLexer.IDENTIFIER -> "1";
            case LambdaLexer.LAMBDA -> "2";
            case LambdaLexer.ASSIGN -> "3";
            case LambdaLexer.COMMA -> "4";
            case LambdaLexer.COLON -> "5";
            case LambdaLexer.PLUS -> "6";
            case LambdaLexer.LPAREN -> "7";
            case LambdaLexer.RPAREN -> "8";
            case LambdaLexer.STAR -> "9";
            case LambdaLexer.NUMBER -> "10";
            case LambdaLexer.SEMICOLON -> "11";
            case LambdaLexer.MINUS -> "12";
            case LambdaLexer.SLASH -> "13";
            case LambdaLexer.UNKNOWN -> "0";
            default -> "?";
        };
    }

    private static String getTokenTypeName(int tokenType) {
        return switch (tokenType) {
            case LambdaLexer.IDENTIFIER -> "идентификатор";
            case LambdaLexer.LAMBDA -> "ключевое слово lambda";
            case LambdaLexer.ASSIGN -> "знак равенства";
            case LambdaLexer.COMMA -> "запятая";
            case LambdaLexer.COLON -> "двоеточие";
            case LambdaLexer.PLUS -> "знак сложения";
            case LambdaLexer.MINUS -> "знак вычитания";
            case LambdaLexer.LPAREN -> "открывающая скобка";
            case LambdaLexer.RPAREN -> "закрывающая скобка";
            case LambdaLexer.STAR -> "знак умножения";
            case LambdaLexer.SLASH -> "знак деления";
            case LambdaLexer.SEMICOLON -> "точка с запятой";
            case LambdaLexer.NUMBER -> "число";
            case LambdaLexer.UNKNOWN -> "недопустимый символ";
            default -> "неизвестный токен";
        };
    }
}
