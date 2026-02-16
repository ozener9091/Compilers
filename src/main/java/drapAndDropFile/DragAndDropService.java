package drapAndDropFile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextArea;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DragAndDropService {

    public static ObjectProperty<File> setupDragAndDrop(TextArea textArea) {

        ObjectProperty<File> fileProperty = new SimpleObjectProperty<>();

        textArea.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                File file = dragboard.getFiles().getFirst();
                if (file.getName().toLowerCase().endsWith(".txt")) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        textArea.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles()) {
                List<File> files = dragboard.getFiles();
                if (!files.isEmpty()) {
                    File file = files.getFirst();
                    if (file.getName().toLowerCase().endsWith(".txt")) {
                        try {
                            String content = Files.readString(file.toPath());
                            textArea.setText(content);

                            fileProperty.set(file);

                            success = true;
                        } catch (IOException e) {
                            textArea.setText("Ошибка чтения файла: " + e.getMessage());
                        }
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return fileProperty;
    }
}