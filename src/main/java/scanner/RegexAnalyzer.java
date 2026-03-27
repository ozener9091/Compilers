package scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexAnalyzer {

    public static final String IDENTIFIER_PATTERN = "[a-zA-Z_$][0-9]*";
    public static final String USERNAME_PATTERN = "[a-zA-Z][a-zA-Z0-9]{1,29}";
    public static final String LONGITUDE_PATTERN =
        "(?:1[0-7][0-9]|[1-9]?[0-9])°[0-5][0-9]'[0-5][0-9]\"[EW]";

    private RegexAnalyzer() {}

    /**
     * Найти все совпадения для идентификатора.
     */
    public static List<RegexMatchEntry> findIdentifiers(String text) {
        return findMatches(text, IDENTIFIER_PATTERN, "Идентификатор");
    }

    /**
     * Найти все совпадения для имени пользователя.
     */
    public static List<RegexMatchEntry> findUsernames(String text) {
        return findMatches(text, USERNAME_PATTERN, "Имя пользователя");
    }

    /**
     * Найти все совпадения для долготы.
     */
    public static List<RegexMatchEntry> findLongitudes(String text) {
        return findMatches(text, LONGITUDE_PATTERN, "Долгота");
    }

    /**
     * Общая метода для поиска всех совпадений по шаблону.
     */
    private static List<RegexMatchEntry> findMatches(String text, String patternStr, String type) {
        List<RegexMatchEntry> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String matchedText = matcher.group();
            int startPos = matcher.start();
            int length = matchedText.length();
            matches.add(new RegexMatchEntry(matchedText, startPos, length));
        }

        return matches;
    }
}
