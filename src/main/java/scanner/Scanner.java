package scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, Integer> tokenDict = Map.ofEntries(
            Map.entry("id", 1),
            Map.entry("lambda", 2),
            Map.entry("=", 3),
            Map.entry(",", 4),
            Map.entry(":", 5),
            Map.entry("+", 6),
            Map.entry("(", 7),
            Map.entry(")", 8),
            Map.entry("*", 9),
            Map.entry(" ", 10),
            Map.entry(";", 11)
    );

    private static final Map<String, String> tokenTypeNames = Map.ofEntries(
            Map.entry("id", "идентификатор"),
            Map.entry("lambda", "ключевое слово lambda"),
            Map.entry("=", "знак равенства"),
            Map.entry(",", "запятая"),
            Map.entry(":", "двоеточие"),
            Map.entry("+", "знак сложения"),
            Map.entry("(", "открывающая скобка"),
            Map.entry(")", "закрывающая скобка"),
            Map.entry("*", "знак умножения"),
            Map.entry(" ", "пробел"),
            Map.entry(";", "точка с запятой")
    );

    public static class TokenInfo {
        private String code;
        private String tokenType;
        private String token;
        private String location;

        public TokenInfo(String code, String tokenType, String token, String location) {
            this.code = code;
            this.tokenType = tokenType;
            this.token = token;
            this.location = location;
        }

        public String getCode() { return code; }
        public String getTokenType() { return tokenType; }
        public String getToken() { return token; }
        public String getLocation() { return location; }
    }

    public static class ErrorInfo {
        private String type;
        private String content;
        private String page;

        public ErrorInfo(String type, String content, String page) {
            this.type = type;
            this.content = content;
            this.page = page;
        }

        public String getType() { return type; }
        public String getContent() { return content; }
        public String getPage() { return page; }
    }

    public static List<TokenInfo> getTokenList(String input) {
        List<TokenInfo> tokens = new ArrayList<>();
        int position = 0;
        int lineNumber = 1;
        int columnNumber = 1;

        while (position < input.length()) {
            char currentChar = input.charAt(position);

            // Пропускаем пробелы
            if (currentChar == ' ') {
                position++;
                columnNumber++;
                continue;
            }

            if (currentChar == '\n') {
                lineNumber++;
                columnNumber = 1;
                position++;
                continue;
            }

            String currentToken = "";
            int startColumn = columnNumber;

            // Обработка буквенных токенов
            if (Character.isLetter(currentChar)) {
                while (position < input.length() &&
                        (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                    currentToken += input.charAt(position);
                    position++;
                    columnNumber++;
                }

                String location = lineNumber + ":" + startColumn;

                // Является ли это ключевым словом lambda
                if (currentToken.equals("lambda")) {
                    String code = String.valueOf(tokenDict.get("lambda"));
                    String tokenType = tokenTypeNames.get("lambda");
                    tokens.add(new TokenInfo(code, tokenType, currentToken, location));
                } else {
                    // Иначе это идентификатор (id)
                    String code = String.valueOf(tokenDict.get("id"));
                    String tokenType = tokenTypeNames.get("id");
                    tokens.add(new TokenInfo(code, tokenType, currentToken, location));
                }
            }
            else {
                currentToken = String.valueOf(currentChar);
                String location = lineNumber + ":" + startColumn;
                position++;
                columnNumber++;

                // Проверяем, есть ли символ в словаре
                if (tokenDict.containsKey(currentToken)) {
                    String code = String.valueOf(tokenDict.get(currentToken));
                    String tokenType = tokenTypeNames.getOrDefault(currentToken, currentToken);
                    tokens.add(new TokenInfo(code, tokenType, currentToken, location));
                } else {
                    // Неизвестный символ - добавляем в ошибки
                }
            }
        }

        return tokens;
    }

    public static List<ErrorInfo> getErrorList(String input) {
        List<ErrorInfo> errors = new ArrayList<>();
        int position = 0;
        int lineNumber = 1;
        int columnNumber = 1;

        while (position < input.length()) {
            char currentChar = input.charAt(position);

            if (currentChar == ' ') {
                position++;
                columnNumber++;
                continue;
            }

            if (currentChar == '\n') {
                lineNumber++;
                columnNumber = 1;
                position++;
                continue;
            }

            String currentToken = "";
            int startColumn = columnNumber;

            // Обработка буквенных токенов
            if (Character.isLetter(currentChar)) {
                while (position < input.length() &&
                        (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                    currentToken += input.charAt(position);
                    position++;
                    columnNumber++;
                }

                // Проверяем, является ли это допустимым токеном
                if (!currentToken.equals("lambda") && !isValidIdentifier(currentToken)) {
                    String page = lineNumber + ":" + startColumn;
                    String errorType = "Лексическая ошибка";
                    String errorContent = "Недопустимый идентификатор: '" + currentToken + "'";
                    errors.add(new ErrorInfo(errorType, errorContent, page));
                }
            }
            else {
                currentToken = String.valueOf(currentChar);
                String page = lineNumber + ":" + startColumn;
                position++;
                columnNumber++;

                // Проверяем, есть ли символ в словаре
                if (!tokenDict.containsKey(currentToken)) {
                    String errorType = "Лексическая ошибка";
                    String errorContent = "Недопустимый символ: '" + currentToken + "'";
                    errors.add(new ErrorInfo(errorType, errorContent, page));
                }
            }
        }

        return errors;
    }

    private static boolean isValidIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }
        // Идентификатор должен начинаться с буквы
        if (!Character.isLetter(identifier.charAt(0))) {
            return false;
        }
        // Остальные символы могут быть буквами, цифрами или подчеркиванием
        for (int i = 1; i < identifier.length(); i++) {
            char c = identifier.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }
}