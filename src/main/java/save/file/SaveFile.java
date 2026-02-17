package save.file;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import multipleTabsService.FileTab;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SaveFile {

    public static void saveFile(ObservableList<FileTab> fileTabs, TabPane tabPane, Label statusLabel, FileTab fileTab) {



        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) return;

        if(fileTab == null){
            fileTab = findFileTabByTab(fileTabs, selectedTab);
        }
        File file = fileTab.getFile();
        if (file == null) {
            saveFileAs(tabPane, fileTab, statusLabel);
        } else {
            writeToFile(fileTab, file, statusLabel);
        }
    }

    public static void saveFileAs(TabPane tabPane, FileTab fileTab, Label statusLabel) {

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("document.txt");

        Window window = fileTab.getCodeArea().getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            writeToFile(fileTab, file, statusLabel);
        }

    }

    public static void writeToFile(FileTab fileTab, File file, Label statusLabel) {
        try {
            String content = fileTab.getCodeArea().getText();
            Files.writeString(file.toPath(), content);
            statusLabel.setText(file.getAbsolutePath());
            fileTab.setModified(false);
            fileTab.getTab().setText(file.getName());

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static FileTab findFileTabByTab(ObservableList<FileTab> fileTabs, Tab tab) {
        return fileTabs.stream()
                .filter(ft -> ft.getTab().equals(tab))
                .findFirst()
                .orElse(null);
    }
}
