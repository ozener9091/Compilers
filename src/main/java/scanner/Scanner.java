package scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scanner {

    public enum TokenKind {
        IDENTIFIER,
        NUMBER,
        LAMBDA,
        ASSIGN,
        COMMA,
        COLON,
        PLUS,
        MINUS,
        LPAREN,
        RPAREN,
        STAR,
        SLASH,
        SEMICOLON,
        EOF
    }

    private static final Map<TokenKind, Integer> tokenDict = Map.ofEntries(
            Map.entry(TokenKind.IDENTIFIER, 1),
            Map.entry(TokenKind.NUMBER, 10),
            Map.entry(TokenKind.LAMBDA, 2),
            Map.entry(TokenKind.ASSIGN, 3),
            Map.entry(TokenKind.COMMA, 4),
            Map.entry(TokenKind.COLON, 5),
            Map.entry(TokenKind.PLUS, 6),
            Map.entry(TokenKind.MINUS, 12),
            Map.entry(TokenKind.LPAREN, 7),
            Map.entry(TokenKind.RPAREN, 8),
            Map.entry(TokenKind.STAR, 9),
            Map.entry(TokenKind.SLASH, 13),
            Map.entry(TokenKind.SEMICOLON, 11)
    );

    private static final Map<TokenKind, String> tokenTypeNames = Map.ofEntries(
            Map.entry(TokenKind.IDENTIFIER, "идентификатор"),
            Map.entry(TokenKind.NUMBER, "число"),
            Map.entry(TokenKind.LAMBDA, "ключевое слово lambda"),
            Map.entry(TokenKind.ASSIGN, "знак равенства"),
            Map.entry(TokenKind.COMMA, "запятая"),
            Map.entry(TokenKind.COLON, "двоеточие"),
            Map.entry(TokenKind.PLUS, "знак сложения"),
            Map.entry(TokenKind.MINUS, "знак вычитания"),
            Map.entry(TokenKind.LPAREN, "открывающая скобка"),
            Map.entry(TokenKind.RPAREN, "закрывающая скобка"),
            Map.entry(TokenKind.STAR, "знак умножения"),
            Map.entry(TokenKind.SLASH, "знак деления"),
            Map.entry(TokenKind.SEMICOLON, "точка с запятой")
    );

    public static class Lexeme {
        private final TokenKind kind;
        private final String lexeme;
        private final int line;
        private final int column;

        public Lexeme(TokenKind kind, String lexeme, int line, int column) {
            this.kind = kind;
            this.lexeme = lexeme;
            this.line = line;
            this.column = column;
        }

        public TokenKind getKind() {
            return kind;
        }

        public String getLexeme() {
            return lexeme;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public String getLocation() {
            return line + ":" + column;
        }

        public int getLength() {
            return lexeme == null ? 0 : lexeme.length();
        }

        public boolean isEof() {
            return kind == TokenKind.EOF;
        }
    }

    public static class TokenInfo {
        private final String code;
        private final String tokenType;
        private final String token;
        private final String location;

        public TokenInfo(String code, String tokenType, String token, String location) {
            this.code = code;
            this.tokenType = tokenType;
            this.token = token;
            this.location = location;
        }

        public String getCode() {
            return code;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getToken() {
            return token;
        }

        public String getLocation() {
            return location;
        }
    }

    public static class ErrorInfo {
        private final String type;
        private final String fragment;
        private final String description;
        private final int line;
        private final int column;
        private final int highlightLength;

        public ErrorInfo(String type, String content, String page) {
            this.type = type;
            this.description = content;
            this.fragment = extractFragment(content);

            int parsedLine = 1;
            int parsedColumn = 1;
            if (page != null && page.contains(":")) {
                String[] parts = page.split(":");
                if (parts.length >= 2) {
                    try {
                        parsedLine = Integer.parseInt(parts[0]);
                        parsedColumn = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            this.line = parsedLine;
            this.column = parsedColumn;
            this.highlightLength = calculateHighlightLength(this.fragment);
        }

        public ErrorInfo(String type, String fragment, String description, int line, int column) {
            this(type, fragment, description, line, column, calculateHighlightLength(fragment));
        }

        public ErrorInfo(String type, String fragment, String description, int line, int column, int highlightLength) {
            this.type = type;
            this.fragment = fragment == null ? "" : fragment;
            this.description = description;
            this.line = line;
            this.column = column;
            this.highlightLength = Math.max(highlightLength, 0);
        }

        public String getType() {
            return type;
        }

        public String getContent() {
            return description;
        }

        public String getPage() {
            return getLocation();
        }

        public String getFragment() {
            return fragment;
        }

        public String getDescription() {
            return description;
        }

        public String getLocation() {
            return line + ":" + column;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public int getHighlightLength() {
            return highlightLength;
        }

        private static String extractFragment(String content) {
            if (content == null || content.isEmpty()) {
                return "";
            }

            int startQuote = content.indexOf('\'');
            int endQuote = content.lastIndexOf('\'');
            if (startQuote >= 0 && endQuote > startQuote) {
                return content.substring(startQuote + 1, endQuote);
            }

            return content;
        }

        private static int calculateHighlightLength(String fragment) {
            if (fragment == null || fragment.isBlank() || "<конец ввода>".equals(fragment)) {
                return 0;
            }
            return fragment.length();
        }
    }

    public static class LexicalResult {
        private final List<Lexeme> lexemes;
        private final List<TokenInfo> tokens;
        private final List<ErrorInfo> errors;

        public LexicalResult(List<Lexeme> lexemes, List<TokenInfo> tokens, List<ErrorInfo> errors) {
            this.lexemes = lexemes;
            this.tokens = tokens;
            this.errors = errors;
        }

        public List<Lexeme> getLexemes() {
            return lexemes;
        }

        public List<TokenInfo> getTokens() {
            return tokens;
        }

        public List<ErrorInfo> getErrors() {
            return errors;
        }
    }

    public static LexicalResult analyze(String input) {
        List<Lexeme> lexemes = new ArrayList<>();
        List<TokenInfo> tokens = new ArrayList<>();
        List<ErrorInfo> errors = new ArrayList<>();

        int position = 0;
        int lineNumber = 1;
        int columnNumber = 1;

        while (position < input.length()) {
            char currentChar = input.charAt(position);

            if (currentChar == '\r') {
                position++;
                continue;
            }

            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    lineNumber++;
                    columnNumber = 1;
                } else {
                    columnNumber++;
                }
                position++;
                continue;
            }

            int startColumn = columnNumber;

            if (Character.isLetter(currentChar)) {
                StringBuilder builder = new StringBuilder();
                while (position < input.length()) {
                    char symbol = input.charAt(position);
                    if (!Character.isLetterOrDigit(symbol) && symbol != '_') {
                        break;
                    }
                    builder.append(symbol);
                    position++;
                    columnNumber++;
                }

                String currentToken = builder.toString();
                TokenKind kind = currentToken.equals("lambda") ? TokenKind.LAMBDA : TokenKind.IDENTIFIER;
                addToken(lexemes, tokens, kind, currentToken, lineNumber, startColumn);
                continue;
            }

            if (Character.isDigit(currentChar)) {
                StringBuilder builder = new StringBuilder();
                while (position < input.length() && Character.isDigit(input.charAt(position))) {
                    builder.append(input.charAt(position));
                    position++;
                    columnNumber++;
                }

                addToken(lexemes, tokens, TokenKind.NUMBER, builder.toString(), lineNumber, startColumn);
                continue;
            }

            TokenKind symbolKind = switch (currentChar) {
                case '=' -> TokenKind.ASSIGN;
                case ',' -> TokenKind.COMMA;
                case ':' -> TokenKind.COLON;
                case '+' -> TokenKind.PLUS;
                case '-' -> TokenKind.MINUS;
                case '(' -> TokenKind.LPAREN;
                case ')' -> TokenKind.RPAREN;
                case '*' -> TokenKind.STAR;
                case '/' -> TokenKind.SLASH;
                case ';' -> TokenKind.SEMICOLON;
                default -> null;
            };

            if (symbolKind != null) {
                addToken(lexemes, tokens, symbolKind, String.valueOf(currentChar), lineNumber, startColumn);
            } else {
                String fragment = String.valueOf(currentChar);
                String description = "Недопустимый символ '" + fragment + "'.";
                errors.add(new ErrorInfo("Лексическая ошибка", fragment, description, lineNumber, startColumn));
            }

            position++;
            columnNumber++;
        }

        lexemes.add(new Lexeme(TokenKind.EOF, "<EOF>", lineNumber, columnNumber));
        return new LexicalResult(lexemes, tokens, errors);
    }

    public static List<Lexeme> getLexemes(String input) {
        return analyze(input).getLexemes();
    }

    public static List<TokenInfo> getTokenList(String input) {
        return analyze(input).getTokens();
    }

    public static List<ErrorInfo> getErrorList(String input) {
        return analyze(input).getErrors();
    }

    private static void addToken(
            List<Lexeme> lexemes,
            List<TokenInfo> tokens,
            TokenKind kind,
            String lexeme,
            int line,
            int column
    ) {
        lexemes.add(new Lexeme(kind, lexeme, line, column));
        tokens.add(toTokenInfo(kind, lexeme, line, column));
    }

    private static TokenInfo toTokenInfo(TokenKind kind, String token, int line, int column) {
        return new TokenInfo(
                String.valueOf(tokenDict.get(kind)),
                tokenTypeNames.get(kind),
                token,
                line + ":" + column
        );
    }
}
