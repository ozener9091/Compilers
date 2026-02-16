package hotkeysService;

import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;

public class HotkeysService {
    public static void addHotkey(VBox mainWindow, KeyCombination keyCombination, Runnable action) {
        Scene scene = mainWindow.getScene();
        if (scene != null) {
            scene.getAccelerators().put(keyCombination, action);
        } else {
            mainWindow.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getAccelerators().put(keyCombination, action);
                }
            });
        }
    }
}
