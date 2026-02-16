package save.file;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SaveFile {

    @FXML
    public static void saveAsFile(CodeArea codeArea, Label label) {
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
            saveFile(codeArea, file, label);
        }
    }

    public static void saveFile(CodeArea codeArea, File file, Label statusLabel) {
        try {
            String content = codeArea.getText();
            Files.writeString(file.toPath(), content);
            statusLabel.setText(file.getAbsolutePath());

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
