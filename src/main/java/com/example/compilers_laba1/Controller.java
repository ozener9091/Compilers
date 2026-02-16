package com.example.compilers_laba1;
import drapAndDropFile.DragAndDropService;
import highlighting.HighlightingService;
import hotkeysService.HotkeysService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import localization.Localization;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import save.file.SaveFile;
import exceptions.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

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
    English, Russian
}

public class Controller implements Initializable {

    //  Главное окно
    @FXML
    private VBox mainWindow;

    //  Меню файл
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

    //  Меню правка
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

    //  Меню справка
    @FXML
    private Menu aboutLabel;
    @FXML
    private MenuItem userManualButton;
    @FXML
    private MenuItem aboutButton;

    //  Меню язык
    @FXML
    private Menu languageLabel;
    @FXML
    private RadioMenuItem englishSelectButton;
    @FXML
    private RadioMenuItem russianSelectButton;

    //  Панель инструментов
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

    //  Поле состояния
    @FXML
    private Label statusLabel;

    //  Поле ввода
    @FXML
    private CodeArea codeArea;

    //  Меню модулей
    @FXML
    private Menu analyzerLabel;
    @FXML
    private Menu pseudoCodeLabel;
    @FXML
    private Menu controlFlowGraphLabel;

    //  Поле вывода
    @FXML
    private Label outputLabel;

    //  Таблица с ошибками
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
    private double inputCurrentFontSize = 14;
    private double outputCurrentFontSize = 14;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        initWindowStyle();
        codeArea.setStyle("-fx-font-size: " + inputCurrentFontSize + "px;");
        outputLabel.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");

        addAllToLocalizationList();
        getErrorService();
        getDragAndDropService();
        initLineNumber();
        initHotkeys();

    }

    private void addAllToLocalizationList() {

        //  Меню
        localizationList.add(fileLabel);
        localizationList.add(editLabel);
        localizationList.add(aboutLabel);
        localizationList.add(languageLabel);

        //  Панель файл
        localizationList.add(createButton);
        localizationList.add(loadFileButton);
        localizationList.add(saveButton);
        localizationList.add(saveAsButton);
        localizationList.add(exitButton);

        //  Меню правка
        localizationList.add(undoButton);
        localizationList.add(cutButton);
        localizationList.add(copyButton);
        localizationList.add(pasteButton);
        localizationList.add(removeButton);
        localizationList.add(selectAllButton);

        //  Меню справка
        localizationList.add(userManualButton);
        localizationList.add(aboutButton);

        //  Меню язык
        localizationList.add(englishSelectButton);
        localizationList.add(russianSelectButton);

        //  Панель инструментов
        localizationList.add(createTooltip);
        localizationList.add(openTooltip);
        localizationList.add(saveTooltip);
        localizationList.add(undoTooltip);
        localizationList.add(copyTooltip);
        localizationList.add(cutTooltip);
        localizationList.add(pasteTooltip);

        //  Меню вывода
        localizationList.add(analyzerLabel);
        localizationList.add(pseudoCodeLabel);
        localizationList.add(controlFlowGraphLabel);

        //  Таблица с ошибками
        localizationList.add(typeColumn);
        localizationList.add(contentColumn);
        localizationList.add(pageColumn);
    }
    private void getErrorService(){
        exceptionOutput = new ExceptionOutput(errorTable);
        ErrorTable.initErrorTable(typeColumn, contentColumn, pageColumn, errorTable);
    }
    private void getDragAndDropService(){
        choosenFileProperty = DragAndDropService.setupDragAndDrop(codeArea);
    }
    private void initLineNumber(){
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }
    private void initWindowStyle(){
        HighlightingService.setupSyntaxHighlighting(codeArea);
    }
    private void initHotkeys(){
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+N"), this::createClick);
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+O"), this::loadFileClick);
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+S"), this::saveFileClick);
    }

    @FXML
    protected void createClick(){
        choosenFile = null;
        codeArea.clear();
    }

    @FXML
    protected void loadFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt")
        );
        choosenFile = fileChooser.showOpenDialog(codeArea.getScene().getWindow());

        if (choosenFile != null) {
            try {
                String content = Files.readString(choosenFile.toPath());
                codeArea.replaceText(content);
                statusLabel.setText(choosenFile.getAbsolutePath());
            } catch (IOException ex) {
                exceptionOutput.ThrowException("Ошибка чтения файла.");
            }
        }
    }

    @FXML
    protected void saveFileClick() {

        choosenFile = choosenFileProperty.get();
        if (choosenFile != null) {
            SaveFile.saveFile(codeArea, choosenFile, statusLabel);
        } else {
            SaveFile.saveAsFile(codeArea, statusLabel);
        }
    }

    @FXML
    protected void saveAsFileClick(){
        SaveFile.saveAsFile(codeArea,  statusLabel);
    }

    @FXML
    protected void exitClick() {
        if (choosenFile == null || codeArea.isEditable()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText("Сохранение файла");
            alert.setContentText("Сохранить файл перед выходом?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    SaveFile.saveAsFile(codeArea, statusLabel);
                } else {
                    Platform.exit();
                }
            });
        }
        else Platform.exit();
    }

    @FXML
    protected void undoClick() { codeArea.undo(); }

    @FXML
    protected void cutClick(){ codeArea.cut(); }

    @FXML
    protected void copyClick(){ codeArea.copy(); }

    @FXML
    protected  void pasteClick(){ codeArea.paste(); }

    @FXML
    protected void removeClick() { codeArea.clear(); }

    @FXML
    protected void selectAllClick() { codeArea.selectAll(); }

    @FXML
    protected void aboutClick() {
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
    protected void userManualClick() {
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
    protected void increaseInputClick() {
        if (inputCurrentFontSize >= 24) return;
        inputCurrentFontSize += 2;
        codeArea.setStyle("-fx-font-size: " + inputCurrentFontSize + "px;");
    }

    @FXML
    protected void decreaseInputClick() {
        if (inputCurrentFontSize <= 14) return;
        inputCurrentFontSize -= 2;
        codeArea.setStyle("-fx-font-size: " + inputCurrentFontSize + "px;");
    }

    @FXML
    protected  void increaseOutputClick() {
        if (outputCurrentFontSize >= 24) return;
        outputCurrentFontSize += 2;
        outputLabel.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
    }

    @FXML
    protected  void decreaseOutputClick() {
        if (outputCurrentFontSize <= 14) return;
        outputCurrentFontSize -= 2;
        outputLabel.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
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
