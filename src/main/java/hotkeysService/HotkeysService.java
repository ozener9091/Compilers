package hotkeysService;

import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;

public class HotkeysService {
    public static void initHotkeysService(VBox mainWindow, KeyCombination keyCombination, Runnable action) {
        mainWindow.sceneProperty().addListener((scene) -> {
            if (scene != null) {
                mainWindow.getScene().getAccelerators().put(keyCombination, action);
            }
        });
    }
}
