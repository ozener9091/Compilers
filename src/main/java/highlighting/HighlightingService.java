package highlighting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class HighlightingService {

    public static class RegexMatch {
        public final int start;
        public final int end;

        public RegexMatch(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Применить подсветку регулярных выражений.
     * Вызывается при нажатии кнопки.
     */
    public static void applyRegexHighlighting(CodeArea codeArea, List<RegexMatch> matches, String patternStr) {
        String text = codeArea.getText();
        StyleSpans<Collection<String>> spans = computeHighlighting(text, matches, patternStr);
        codeArea.setStyleSpans(0, spans);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text, List<RegexMatch> regexMatches, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);

        // Собираем все диапазоны с их стилями
        List<StyleRange> ranges = new java.util.ArrayList<>();

        // Находим все совпадения по паттерну
        while (matcher.find()) {
            ranges.add(new StyleRange(matcher.start(), matcher.end(), "highlight-yellow"));
        }

        // Сортируем по начальной позиции
        ranges.sort((a, b) -> Integer.compare(a.start, b.start));

        // Строим стили
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
        int pos = 0;

        for (StyleRange range : ranges) {
            if (range.start > pos) {
                builder.add(Collections.emptyList(), range.start - pos);
            }
            builder.add(Collections.singleton(range.styleClass), range.end - range.start);
            pos = Math.max(pos, range.end);
        }

        if (pos < text.length()) {
            builder.add(Collections.emptyList(), text.length() - pos);
        }

        return builder.create();
    }

    private static class StyleRange {
        final int start;
        final int end;
        final String styleClass;

        StyleRange(int start, int end, String styleClass) {
            this.start = start;
            this.end = end;
            this.styleClass = styleClass;
        }
    }
}
