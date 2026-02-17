package multipleTabsService;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static save.file.SaveFile.saveFile;

public class MultipleTabsService {

    public static void createNewTab(TabPane tabPane,
                                    ObservableList<FileTab> fileTabs,
                                    File file,
                                    Label statusLabel) {
        CodeArea codeArea = new CodeArea();
        Tab tab = new Tab();
        tab.setContent(codeArea);
        tab.setClosable(true);

        FileTab fileTab = new FileTab(tab, codeArea, file);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                codeArea.replaceText(content);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        tab.setOnCloseRequest(event -> {
            if (fileTab.isModified()) {
                event.consume();
                askSaveBeforeClose(fileTabs, tabPane, fileTab, statusLabel);
            }
        });
        tab.setOnSelectionChanged(event -> {
            if (fileTab.getFile() != null) {
                statusLabel.setText(fileTab.getFile().getAbsolutePath());
            }
            else {
                statusLabel.setText("File dont exist");
            }
        });

        tab.setOnClosed(event -> fileTabs.remove(fileTab));

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        fileTabs.add(fileTab);

        Platform.runLater(codeArea::requestFocus);
    }

    public static void askSaveBeforeClose(ObservableList<FileTab> fileTabs,
                                           TabPane tabPane,
                                           FileTab fileTab,
                                           Label statusLabel) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Сохранение");
        alert.setHeaderText("Файл \"" + (fileTab.getFile() != null ? fileTab.getFile().getName() : "Новый документ") + "\" был изменён.");
        alert.setContentText("Сохранить изменения перед закрытием?");

        ButtonType saveButton = new ButtonType("Сохранить");
        ButtonType dontSaveButton = new ButtonType("Не сохранять");
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == saveButton) {
                saveFile(fileTabs, tabPane, statusLabel, fileTab);
                tabPane.getTabs().remove(fileTab.getTab());
            } else if (response == dontSaveButton) {
                tabPane.getTabs().remove(fileTab.getTab());
            }
        });
    }

    public static CodeArea getActiveCodeArea(TabPane tabPane) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getContent() instanceof CodeArea) {
            return (CodeArea) selectedTab.getContent();
        }
        return null;
    }

    public static FileTab getActiveFileTab(TabPane tabPane, ObservableList<FileTab> fileTabs) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) return null;
        return fileTabs.stream()
                .filter(ft -> ft.getTab().equals(selectedTab))
                .findFirst()
                .orElse(null);
    }
}
