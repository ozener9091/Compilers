package scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Анализатор текста с помощью регулярных выражений.
 * Содержит шаблоны для поиска идентификаторов, имён пользователей и долготы.
 */
public class RegexAnalyzer {

    /**
     * Идентификатор: начинается с буквы a-zA-Z, $ или _,
     * остальные символы - только цифры.
     * Примеры: a123, Z9, $5, _0
     */
    private static final String IDENTIFIER_PATTERN = "[a-zA-Z_$][0-9]*";

    /**
     * Имя пользователя: 2-30 символов (буквы и цифры),
     * первый символ - буква.
     */
    private static final String USERNAME_PATTERN = "[a-zA-Z][a-zA-Z0-9]{1,29}";

    /**
     * Долгота в формате градусы/минуты/секунды с направлением.
     * Формат: DDD°MM'SS"W или DDD°MM'SS"E
     * Градусы: 0-180, Минуты: 0-59, Секунды: 0-59
     */
    private static final String LONGITUDE_PATTERN = 
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

    /**
     * Получить список позиций для подсветки.
     * Возвращает массив пар (start, end) для выделения.
     */
    public static List<int[]> getHighlightPositions(List<RegexMatchEntry> matches) {
        List<int[]> positions = new ArrayList<>();
        for (RegexMatchEntry match : matches) {
            int start = Integer.parseInt(match.getPosition());
            int length = Integer.parseInt(match.getLength());
            positions.add(new int[]{start, start + length});
        }
        return positions;
    }
}
