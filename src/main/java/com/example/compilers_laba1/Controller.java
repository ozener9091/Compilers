package com.example.compilers_laba1;
import drapAndDropFile.DragAndDropService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import localization.Localization;

import save.file.SaveFile;
import exceptions.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

enum Locale {
    English, Russian;
}

public class Controller implements Initializable {

    @FXML
    private Menu fileLabel;
    @FXML
    private MenuItem createButton;
    @FXML
    private MenuItem loadFileButton;
    @FXML
    private MenuItem saveButton;
    @FXML
    private MenuItem saveAsButton;
    @FXML
    private MenuItem exitButton;

    @FXML
    private Menu editLabel;
    @FXML
    private MenuItem undoButton;
    @FXML
    private MenuItem cutButton;
    @FXML
    private MenuItem copyButton;
    @FXML
    private MenuItem pasteButton;
    @FXML
    private MenuItem removeButton;
    @FXML
    private MenuItem selectAllButton;

    @FXML
    private Menu aboutLabel;
    @FXML
    private MenuItem userManualButton;
    @FXML
    private MenuItem aboutButton;

    @FXML
    private Menu languageLabel;
    @FXML
    private RadioMenuItem englishSelectButton;
    @FXML
    private RadioMenuItem russianSelectButton;

    @FXML
    private Tooltip createTooltip;
    @FXML
    private Tooltip openTooltip;
    @FXML
    private Tooltip saveTooltip;
    @FXML
    private Tooltip undoTooltip;
    @FXML
    private Tooltip copyTooltip;
    @FXML
    private Tooltip cutTooltip;
    @FXML
    private Tooltip pasteTooltip;


    @FXML
    private TextArea textArea;
    @FXML
    private Label outputLabel;

    @FXML
    private TableView<ErrorEntry> errorTable;
    @FXML
    private TableColumn<ErrorEntry, String> typeColumn;
    @FXML
    private TableColumn<ErrorEntry, String> contentColumn;
    @FXML
    private TableColumn<ErrorEntry, String> pageColumn;

    private File choosenFile = null;
    private ObjectProperty<File> choosenFileProperty;
    private Locale locale = Locale.Russian;
    private List<Object> localizationList = new ArrayList<>();
    private ExceptionOutput exceptionOutput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addAllToLocalizationList();
        getErrorService();
        ErrorTable.initErrorTable(typeColumn, contentColumn, pageColumn, errorTable);
        getDragAndDropService();

    }

    private void addAllToLocalizationList() {
        localizationList.add(fileLabel);
        localizationList.add(editLabel);
        localizationList.add(aboutLabel);
        localizationList.add(languageLabel);

        localizationList.add(createButton);
        localizationList.add(loadFileButton);
        localizationList.add(saveButton);
        localizationList.add(saveAsButton);
        localizationList.add(exitButton);

        localizationList.add(undoButton);
        localizationList.add(cutButton);
        localizationList.add(copyButton);
        localizationList.add(pasteButton);
        localizationList.add(removeButton);
        localizationList.add(selectAllButton);

        localizationList.add(userManualButton);
        localizationList.add(aboutButton);

        localizationList.add(createTooltip);
        localizationList.add(openTooltip);
        localizationList.add(saveTooltip);
        localizationList.add(undoTooltip);
        localizationList.add(copyTooltip);
        localizationList.add(cutTooltip);
        localizationList.add(pasteTooltip);

        localizationList.add(englishSelectButton);
        localizationList.add(russianSelectButton);
    }
    private void getErrorService(){
        exceptionOutput = new ExceptionOutput(errorTable);
    }
    private void getDragAndDropService(){
        choosenFileProperty = DragAndDropService.setupDragAndDrop(textArea);
    }



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
                exceptionOutput.ThrowException("Ошибка чтения файла.");
            }
        }
    }

    @FXML
    protected void saveFileClick() {

        choosenFile = choosenFileProperty.get();
        if (choosenFile != null) {
            SaveFile.saveFile(textArea, choosenFile);
        } else {
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
            Alert alert = new Alert(AlertType.CONFIRMATION);
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
    protected void cutClick(){ textArea.cut(); }

    @FXML
    protected void copyClick(){ textArea.copy(); }

    @FXML
    protected  void pasteClick(){ textArea.paste(); }

    @FXML
    protected void removeClick() { textArea.clear(); }

    @FXML
    protected void selectAllClick() { textArea.selectAll(); }

    @FXML
    protected void aboutClick() throws ExceptionOutput {
        Alert alert = new Alert(AlertType.INFORMATION);
        switch (locale) {
            case Russian -> {
                alert.setTitle("О программе");
                alert.setHeaderText("О программе");
                alert.setContentText("""
                Лабораторная работа №1
                Сделал: Ситников В.И.
                Группа: АП-326
                Предмет: Теория формальных языков и компиляторов
                Проверил: Антонянц Е.Н.
                """);
            }
            case English -> {
                alert.setTitle("About program");
                alert.setHeaderText("About program");
                alert.setContentText("""
                Laboratory work №1
                Did: Sitnikov V.I.
                Group: AP-326
                Subject: Theory of formal languages and compilers
                Checked: Antonyants E.N.
                """);
            }
            default -> exceptionOutput.ThrowException("Ошибка поддерживаемого языка.");
        }
        alert.showAndWait();
    }

    @FXML
    protected void userManualClick() throws ExceptionOutput {
        Alert alert = new Alert(AlertType.INFORMATION);
        switch (locale) {
            case Russian -> {
                alert.setTitle("Руководство пользователя");
                alert.setHeaderText("Руководство пользователя");
            }
            case English -> {
                    alert.setTitle("User Manual");
                    alert.setHeaderText("User Manual");
            }
            default -> exceptionOutput.ThrowException("Ошибка поддерживаемого языка.");
        }

        Label linkLabel = new Label("https://github.com/ozener9091/Compilers_Laba1");
        linkLabel.setStyle(
                "-fx-text-fill: blue;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;"
        );

        linkLabel.setOnMouseClicked(_ -> {
            try {
                Desktop.getDesktop().browse(new URI("htasdtps://github.com/ozener9091/Compilers_Laba1"));
            } catch (Exception ex) {
                exceptionOutput.ThrowException("Ошибка открытия ссылки.");
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

    @FXML
    protected void russianSelectClick(){
        for (Object object : localizationList){
            Localization.setLocalization(object, "Russian", exceptionOutput);
        }
        locale = Locale.Russian;
        englishSelectButton.setSelected(false);
        russianSelectButton.setSelected(true);
    }

    @FXML
    protected void englishSelectClick(){
        for (Object object : localizationList){
            Localization.setLocalization(object, "English", exceptionOutput);
        }
        locale = Locale.English;
        russianSelectButton.setSelected(false);
        englishSelectButton.setSelected(true);
    }

}
