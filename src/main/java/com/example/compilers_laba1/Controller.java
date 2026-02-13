package com.example.compilers_laba1;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import save.file.SaveFile;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class Controller {

    @FXML
    private TextArea textArea;
    @FXML
    private Label outputLabel;

    private File choosenFile = null;

    @FXML
    protected void createClick(){
        choosenFile = null;
        textArea.clear();
    }

    @FXML
    protected void loadFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt")
        );
        choosenFile = fileChooser.showOpenDialog(textArea.getScene().getWindow());

        if (choosenFile != null) {
            try {
                String content = Files.readString(choosenFile.toPath());
                textArea.setText(content);
            } catch (IOException ex) {
                textArea.setText("Ошибка чтения файла: " + ex.getMessage());
            }
        }
    }

    @FXML
    protected void saveFileClick() {
        if (choosenFile != null) {
            SaveFile.saveFile(textArea, choosenFile);
        }
        else{
            SaveFile.saveAsFile(textArea);
        }
    }

    @FXML
    protected void saveAsFileClick(){
        SaveFile.saveAsFile(textArea);
    }

    @FXML
    protected void exitClick() {
        if (choosenFile == null || textArea.isEditable()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText("Сохранение файла");
            alert.setContentText("Сохранить файл перед выходом?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    SaveFile.saveAsFile(textArea);
                } else {
                    Platform.exit();
                }
            });
        }
        else Platform.exit();
    }

    @FXML
    protected void undoClick() { textArea.undo(); }

    @FXML
    protected void cutClick(){
        textArea.cut();
    }

    @FXML
    protected void copyClick(){
        textArea.copy();
    }
    @FXML
    protected  void pasteClick(){
        textArea.paste();
    }

    @FXML
    protected void removeClick() { textArea.clear(); }

    @FXML
    protected void selectAllClick() { textArea.selectAll(); }

    @FXML
    protected void aboutClick() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("О программе");
        alert.setContentText("""
                Лабораторная работа №1
                Сделал: Ситников В.И.
                Группа: АП-326
                Предмет: Теория формальных языков и компиляторов
                Проверил: Антонянц Е.Н.
                """);
        alert.showAndWait();
    }

    @FXML
    protected void userManualClick() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Руководство пользователя");
        alert.setHeaderText("Руководство пользователя");

        Label linkLabel = new Label("https://github.com/ozener9091/Compilers_Laba1");
        linkLabel.setStyle(
                "-fx-text-fill: blue;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;"
        );

        linkLabel.setOnMouseClicked(_ -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ozener9091/Compilers_Laba1"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        alert.getDialogPane().setContent(linkLabel);
        alert.showAndWait();
    }

    @FXML
    protected  void increaseInputClick() {
        Font lateFont = textArea.getFont();
        textArea.setFont(Font.font("Arial", lateFont.getSize() + 2));
    }

    @FXML
    protected  void decreaseInputClick() {
        Font lateFont = textArea.getFont();
        textArea.setFont(Font.font("Arial", lateFont.getSize() - 2));
    }

    @FXML
    protected  void increaseOutputClick() {
        Font lateFont = outputLabel.getFont();
        outputLabel.setFont(Font.font("Arial", lateFont.getSize() + 2));
    }

    @FXML
    protected  void decreaseOutputClick() {
        Font lateFont = outputLabel.getFont();
        outputLabel.setFont(Font.font("Arial", lateFont.getSize() - 2));
    }
}
