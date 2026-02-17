package com.example.compilers_laba1;

import hotkeysService.HotkeysService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import localization.Localization;
import multipleTabsService.FileTab;
import multipleTabsService.MultipleTabsService;
import save.file.SaveFile;
import exceptions.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
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

    //  Вкладки
    @FXML
    private TabPane tabPane;
    private final ObservableList<FileTab> fileTabs = FXCollections.observableArrayList();

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


    private Locale locale = Locale.Russian;
    private List<Object> localizationList = new ArrayList<>();
    private ExceptionOutput exceptionOutput;
    private double outputCurrentFontSize = 14;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (tabPane.getTabs().isEmpty()) {
            MultipleTabsService.createNewTab(tabPane, fileTabs, null, statusLabel);
        }

        initWindowStyle();
        addAllToLocalizationList();
        getErrorService();
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

    private void initWindowStyle(){
        outputLabel.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
    }
    private void initHotkeys(){
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+N"), this::createClick);
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+O"), this::loadFileClick);
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+S"), this::saveFileClick);
    }

    @FXML
    protected void createClick(){
        MultipleTabsService.createNewTab(tabPane, fileTabs, null, statusLabel);
    }

    @FXML
    protected void loadFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (file != null) {
            MultipleTabsService.createNewTab(tabPane, fileTabs, file, statusLabel);
        }
    }

    @FXML
    protected void saveFileClick() {
        SaveFile.saveFile(fileTabs, tabPane, statusLabel, null);
    }

    @FXML
    protected void saveAsFileClick(){
        SaveFile.saveFile(fileTabs, tabPane, statusLabel, null);
    }

    @FXML
    protected void exitClick() {

        for(FileTab fileTab : fileTabs){
            if (fileTab.isModified()){
                MultipleTabsService.askSaveBeforeClose(fileTabs, tabPane, fileTab, statusLabel);
            }
        }
        Platform.exit();
    }


    @FXML
    protected void undoClick() {
        MultipleTabsService.getActiveCodeArea(tabPane).undo();
    }
    @FXML
    protected void cutClick(){
        MultipleTabsService.getActiveCodeArea(tabPane).cut();
    }

    @FXML
    protected void copyClick(){
        MultipleTabsService.getActiveCodeArea(tabPane).copy();
    }

    @FXML
    protected  void pasteClick(){
        MultipleTabsService.getActiveCodeArea(tabPane).paste();
    }

    @FXML
    protected void removeClick() {
        MultipleTabsService.getActiveCodeArea(tabPane).clear();
    }

    @FXML
    protected void selectAllClick() {
        MultipleTabsService.getActiveCodeArea(tabPane).selectAll();
    }

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
        MultipleTabsService.getActiveFileTab(tabPane, fileTabs).increaseTextSize();

    }

    @FXML
    protected void decreaseInputClick() {
        MultipleTabsService.getActiveFileTab(tabPane, fileTabs).decreaseTextSize();
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
