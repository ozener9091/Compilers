package localization;

import exceptions.ExceptionOutput;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;

import java.util.HashMap;
import java.util.Map;

public class Localization {

    private static final Map<String, String> russianLocalizationMap = Map.ofEntries(
            Map.entry("Файл", "File"),
            Map.entry("Создать", "Create"),
            Map.entry("Открыть", "Open"),
            Map.entry("Сохранить", "Save"),
            Map.entry("Сохранить как", "Save as"),
            Map.entry("Выход", "Exit"),
            Map.entry("Правка", "Edit"),
            Map.entry("Отменить", "Undo"),
            Map.entry("Вернуть", "Redo"),
            Map.entry("Вырезать", "Cut"),
            Map.entry("Копировать", "Copy"),
            Map.entry("Вставить", "Paste"),
            Map.entry("Удалить", "Remove"),
            Map.entry("Выделить все", "Select all"),
            Map.entry("Справка", "About"),
            Map.entry("Руководство пользователя", "User manual"),
            Map.entry("О программе", "About program"),
            Map.entry("Язык", "Language"),
            Map.entry("Русский", "Russian"),
            Map.entry("Английский", "English"),
            Map.entry("Создать новый файл (Ctrl+N)", "Create new file (Ctrl+N)"),
            Map.entry("Открыть файл (Ctrl+O)", "Open file (Ctrl+O)"),
            Map.entry("Сохранить (Ctrl+S)", "Save (Ctrl+S)"),
            Map.entry("Отменить (Ctrl+Z)", "Undo (Ctrl+Z)"),
            Map.entry("Копировать (Ctrl+C)", "Copy (Ctrl+C)"),
            Map.entry("Вырезать (Ctrl+X)", "Cut (Ctrl+X)"),
            Map.entry("Вставить (Ctrl+V)", "Paste (Ctrl+V)"),
            Map.entry("Анализатор", "Analyzer"),
            Map.entry("Псевдокод", "Pseudocode"),
            Map.entry("Граф потока управления", "Control Flow Graph"),
            Map.entry("Неверный фрагмент", "Invalid fragment"),
            Map.entry("Местоположение", "Location"),
            Map.entry("Описание", "Description")
    );

    private static final Map<String, String> englishToRussianMap = new HashMap<>();
    private static final Map<String, String> russianToEnglishMap = new HashMap<>();

    static {
        for (Map.Entry<String, String> entry : russianLocalizationMap.entrySet()) {
            russianToEnglishMap.put(entry.getKey(), entry.getValue());
            englishToRussianMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static void setLocalization(Object object, String language, ExceptionOutput exceptionOutput) {
        if (object == null) {
            return;
        }

        switch (language) {
            case "English" -> setEnglish(object, exceptionOutput);
            case "Russian" -> setRussian(object, exceptionOutput);
            default -> {
                if (exceptionOutput != null) {
                    exceptionOutput.ThrowException("Ошибка локализации. Неподдерживаемый язык.");
                }
            }
        }
    }

    private static void setEnglish(Object object, ExceptionOutput exceptionOutput) {
        String translatedText = getTranslatedText(object, russianToEnglishMap);
        if (translatedText != null) {
            applyTranslatedText(object, translatedText);
            return;
        }

        if (exceptionOutput != null) {
            exceptionOutput.ThrowException("Ошибка локализации. Неподдерживаемый класс для локализации.");
        }
    }

    private static void setRussian(Object object, ExceptionOutput exceptionOutput) {
        String translatedText = getTranslatedText(object, englishToRussianMap);
        if (translatedText != null) {
            applyTranslatedText(object, translatedText);
            return;
        }

        if (exceptionOutput != null) {
            exceptionOutput.ThrowException("Ошибка локализации. Неподдерживаемый класс для локализации.");
        }
    }

    private static String getTranslatedText(Object object, Map<String, String> dictionary) {
        return switch (object) {
            case Menu menu -> dictionary.get(menu.getText());
            case MenuItem menuItem -> dictionary.get(menuItem.getText());
            case Tooltip tooltip -> dictionary.get(tooltip.getText());
            case Label label -> dictionary.get(label.getText());
            case Button button -> dictionary.get(button.getText());
            case TextArea textArea -> dictionary.get(textArea.getPromptText());
            case TableColumn<?, ?> tableColumn -> dictionary.get(tableColumn.getText());
            default -> null;
        };
    }

    private static void applyTranslatedText(Object object, String translatedText) {
        switch (object) {
            case Menu menu -> menu.setText(translatedText);
            case MenuItem menuItem -> menuItem.setText(translatedText);
            case Tooltip tooltip -> tooltip.setText(translatedText);
            case Label label -> label.setText(translatedText);
            case Button button -> button.setText(translatedText);
            case TextArea textArea -> textArea.setPromptText(translatedText);
            case TableColumn<?, ?> tableColumn -> tableColumn.setText(translatedText);
            default -> {
            }
        }
    }
}
