package localization;

import javafx.scene.control.*;
import java.util.*;
import exceptions.ExceptionOutput;

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
            Map.entry("Тип", "Type"),
            Map.entry("Содержание", "Content"),
            Map.entry("Номер страницы", "Page number")
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
        if (object == null) return;

        switch (language) {
            case "English":
                setEnglish(object, exceptionOutput);
                break;
            case "Russian":
                setRussian(object, exceptionOutput);
                break;
            default:
                if (exceptionOutput != null) {
                    exceptionOutput.ThrowException("Ошибка локализации. Неподдерживаемый язык для локализации.");
            }
        }
    }

    private static void setEnglish(Object object, ExceptionOutput exceptionOutput) {
        switch (object) {
            case Menu menu -> {
                String englishText = russianToEnglishMap.get(menu.getText());
                if (englishText != null) menu.setText(englishText);
            }
            case MenuItem menuItem -> {
                String englishText = russianToEnglishMap.get(menuItem.getText());
                if (englishText != null) menuItem.setText(englishText);
            }
            case Tooltip tooltip -> {
                String englishText = russianToEnglishMap.get(tooltip.getText());
                if (englishText != null) tooltip.setText(englishText);
            }
            case Label label -> {
                String englishText = russianToEnglishMap.get(label.getText());
                if (englishText != null) label.setText(englishText);
            }
            case Button button -> {
                String englishText = russianToEnglishMap.get(button.getText());
                if (englishText != null) button.setText(englishText);
            }
            case TextArea textArea -> {
                String englishText = russianToEnglishMap.get(textArea.getPromptText());
                if (englishText != null) textArea.setPromptText(englishText);
            }
            case TableColumn<?, ?> tableColumn -> {
                String englishText = russianToEnglishMap.get(tableColumn.getText());
                if (englishText != null) tableColumn.setText(englishText);
            }
            default -> {
                if (exceptionOutput != null){
                    exceptionOutput.ThrowException("Ошибка локализации. Неподдерживамый класс для локализации");
                }
            }
        }
    }

    private static void setRussian(Object object, ExceptionOutput exceptionOutput) {
        switch (object) {
            case Menu menu -> {
                String russianText = englishToRussianMap.get(menu.getText());
                if (russianText != null) menu.setText(russianText);
            }
            case MenuItem menuItem -> {
                String russianText = englishToRussianMap.get(menuItem.getText());
                if (russianText != null) menuItem.setText(russianText);
            }
            case Tooltip tooltip -> {
                String russianText = englishToRussianMap.get(tooltip.getText());
                if (russianText != null) tooltip.setText(russianText);
            }
            case Label label -> {
                String russianText = englishToRussianMap.get(label.getText());
                if (russianText != null) label.setText(russianText);
            }
            case Button button -> {
                String russianText = englishToRussianMap.get(button.getText());
                if (russianText != null) button.setText(russianText);
            }
            case TextArea textArea -> {
                String russianPrompt = englishToRussianMap.get(textArea.getPromptText());
                if (russianPrompt != null) textArea.setPromptText(russianPrompt);
            }
            default -> exceptionOutput.ThrowException("Ошибка локализации. Неподдерживаемый класс для локализации.");
        }
    }
}