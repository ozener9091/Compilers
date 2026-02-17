package multipleTabsService;

import drapAndDropFile.DragAndDropService;
import javafx.scene.control.Tab;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;

import static highlighting.HighlightingService.setupSyntaxHighlighting;

public class FileTab {
    private final Tab tab;
    private final CodeArea codeArea;
    private File file;
    private boolean modified;
    private double textSize;

    public FileTab(Tab tab, CodeArea codeArea, File file) {
        this.tab = tab;
        this.codeArea = codeArea;
        this.textSize = 14;
        this.file = file;
        this.setModified(false);

        codeArea.setStyle("-fx-font-size: " + textSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
        DragAndDropService.setupDragAndDrop(codeArea);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        setupSyntaxHighlighting(codeArea);

        if (tab.getText() == null) {
            tab.setText("Новый документ");
        }

        codeArea.textProperty().addListener((obs, old, newText) -> {
            setModified(true);
        });

    }

    public Tab getTab() { return tab; }
    public CodeArea getCodeArea() { return codeArea; }
    public File getFile() { return file; }
    public boolean isModified() { return modified; }

    public void setModified(boolean modified) {
        this.modified = modified;
        String title = (file != null) ? file.getName() : "Новый документ";
        tab.setText(modified ? title + " *" : title);
    }

    public void increaseTextSize() {
        if (textSize >= 24) return;
        textSize += 2;
        codeArea.setStyle("-fx-font-size: " + textSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
    }

    public void decreaseTextSize() {
        if (textSize <= 14) return;
        textSize -= 2;
        codeArea.setStyle("-fx-font-size: " + textSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
    }

}
