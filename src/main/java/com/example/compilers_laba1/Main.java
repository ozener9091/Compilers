package com.example.compilers_laba1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import multipleTabsService.FileTab;
import multipleTabsService.MultipleTabsService;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("compilers-main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        Controller controller = fxmlLoader.getController();
        controller.setStage(stage);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/editor.css")).toExternalForm());
        stage.setTitle("Compiler Laboratory #1");
        stage.setScene(scene);
        stage.show();

    }
}
