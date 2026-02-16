package save.file;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SaveFile {

    @FXML
    public static void saveAsFile(CodeArea codeArea) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("document.txt");

        Window window = codeArea.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveFile(codeArea, file);
        }
    }

    public static void saveFile(CodeArea codeArea, File file) {
        try {
            String content = codeArea.getText();
            Files.writeString(file.toPath(), content);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
