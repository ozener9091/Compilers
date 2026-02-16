package highlighting;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class HighlightingService {

    public static void setupSyntaxHighlighting(CodeArea codeArea) {
        String[] keywords = new String[] {
                "if", "else", "while", "for", "switch", "case", "break", "continue",
                "return", "int", "float", "double", "char", "string", "boolean", "void", "class",
                "private", "protected", "public", "this", "try", "static"
        };

        String keywordPattern = "\\b(" + String.join("|", keywords) + ")\\b";
        String stringPattern = "\"([^\"\\\\]|\\\\.)*\"";

        Pattern pattern = Pattern.compile(
                "(?<KEYWORD>" + keywordPattern + ")|(?<STRING>" + stringPattern + ")"
        );

        codeArea.multiPlainChanges().subscribe(changes -> {
            String text = codeArea.getText();
            StyleSpans<Collection<String>> spans = computeHighlighting(text, pattern);
            codeArea.setStyleSpans(0, spans);
        });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("STRING")  != null ? "string"  :
                                    null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

        return spansBuilder.create();
    }
}



