package scanner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FlexAdapter {

    private static final String PARSER_EXE_PATH = ".\\flexbison\\lexer.exe"; // путь к исполняемому файлу

    public static Result parse(String input) throws IOException, InterruptedException {
        List<Scanner.TokenInfo> tokens = new ArrayList<>();
        List<Scanner.ErrorInfo> errors = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder(PARSER_EXE_PATH);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Передаём входной текст в процесс
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(input);
            writer.write("\n");
            writer.flush();
        }

        // Читаем вывод процесса
        List<String> outputLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLines.add(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Внешний парсер завершился с кодом: " + exitCode);
        }

        // Парсим каждую строку вывода
        for (String line : outputLines) {
            if (line.startsWith("TOKEN|")) {
                parseTokenLine(line, tokens);
            } else if (line.startsWith("ERROR|")) {
                parseErrorLine(line, errors);
            } else {
                System.out.println("Неизвестный вывод: " + line);
            }
        }

        return new Result(tokens, errors);
    }

    private static void parseTokenLine(String line, List<Scanner.TokenInfo> tokens) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 5) {
            String code = parts[1];
            String tokenType = parts[2];
            String token = parts[3];
            String location = parts[4];
            tokens.add(new Scanner.TokenInfo(code, tokenType, token, location));
        } else {
            System.err.println("Некорректная строка токена: " + line);
        }
    }

    private static void parseErrorLine(String line, List<Scanner.ErrorInfo> errors) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 4) {
            String type = parts[1];
            String content = parts[2];
            String page = parts[3];
            errors.add(new Scanner.ErrorInfo(type, content, page));
        } else {
            System.err.println("Некорректная строка ошибки: " + line);
        }
    }

    public static class Result {
        private final List<Scanner.TokenInfo> tokens;
        private final List<Scanner.ErrorInfo> errors;

        public Result(List<Scanner.TokenInfo> tokens, List<Scanner.ErrorInfo> errors) {
            this.tokens = tokens;
            this.errors = errors;
        }

        public List<Scanner.TokenInfo> getTokens() {
            return tokens;
        }

        public List<Scanner.ErrorInfo> getErrors() {
            return errors;
        }
    }
}