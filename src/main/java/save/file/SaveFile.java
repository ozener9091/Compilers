package save.file;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SaveFile {

    @FXML
    public static void saveAsFile(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("document.txt");

        Window window = textArea.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveFile(textArea, file);
        }
    }

    public static void saveFile(TextArea textArea, File file) {
        try {
            String content = textArea.getText();
            Files.writeString(file.toPath(), content);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
