package com.example.compilers_laba1;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import hotkeysService.*;
import localization.*;
import org.fxmisc.richtext.CodeArea;
import scanner.*;
import exceptions.*;
import save.file.*;
import multipleTabsService.*;
import highlighting.HighlightingService;

import java.util.ArrayList;
import java.util.List;


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
    private MenuItem returnButton;
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

    //  Пуск
    @FXML
    private MenuItem runButton;
    @FXML
    private MenuItem runFlexBisonButton;
    @FXML
    private MenuItem runAntlrButton;

    //  Меню Регулярки
    @FXML
    private Menu regexLabel;
    @FXML
    private MenuItem regexIdentifierButton;
    @FXML
    private MenuItem regexUsernameButton;
    @FXML
    private MenuItem regexLongitudeButton;

    //  Меню Вывод
    @FXML
    private Menu outputLabel;

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
    private Tooltip returnTooltip;
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

    //  Таблица с результатами регулярных выражений
    @FXML
    private TableView<RegexMatchEntry> regexTable;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexMatchColumn;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexPositionColumn;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexLengthColumn;


    private Locale locale = Locale.Russian;
    private List<Object> localizationList = new ArrayList<>();
    private ExceptionOutput exceptionOutput;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (tabPane.getTabs().isEmpty()) {
            MultipleTabsService.createNewTab(tabPane, fileTabs, null, statusLabel);
        }

        initWindowStyle();
        addAllToLocalizationList();
        exceptionOutput = new ExceptionOutput();
        getOutputService();
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
        localizationList.add(returnButton);
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
        localizationList.add(returnTooltip);
        localizationList.add(copyTooltip);
        localizationList.add(cutTooltip);
        localizationList.add(pasteTooltip);

        //  Меню Пуск
        localizationList.add(runButton);
        localizationList.add(runFlexBisonButton);
        localizationList.add(runAntlrButton);

        //  Меню Регулярки
        localizationList.add(regexLabel);
        localizationList.add(regexIdentifierButton);
        localizationList.add(regexUsernameButton);
        localizationList.add(regexLongitudeButton);
    }

    private void getOutputService(){
        RegexOutputTable.initRegexTable(regexMatchColumn, regexPositionColumn, regexLengthColumn, regexTable);
    }

    private void initWindowStyle(){
        // Стили больше не требуются
    }

    private void initHotkeys(){
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+N"), this::createClick);
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+O"), this::loadFileClick);
        HotkeysService.addHotkey(mainWindow, KeyCombination.valueOf("Ctrl+S"), this::saveFileClick);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            for(FileTab fileTab : fileTabs){
                if (fileTab.isModified()){
                    MultipleTabsService.askSaveBeforeClose(fileTabs, tabPane, fileTab, statusLabel);
                }
            }
        });
    }

    private int getPositionFromLineAndColumn(String text, int line, int column) {
        String[] lines = text.split("\n", -1);

        int position = 0;
        for (int i = 0; i < Math.min(line - 1, lines.length); i++) {
            position += lines[i].length() + 1; // +1 для символа новой строки
        }

        if (line - 1 < lines.length) {
            position += Math.min(column - 1, lines[line - 1].length());
        }

        return Math.min(position, text.length());
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
    protected void returnClick(){
        MultipleTabsService.getActiveCodeArea(tabPane).redo();
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

        linkLabel.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ozener9091/Compilers_Laba1"));
            } catch (Exception ex) {
                exceptionOutput.ThrowException("Ошибка открытия ссылки.");
            }
        });
        alert.getDialogPane().setContent(linkLabel);
        alert.showAndWait();
    }

    @FXML
    protected void runClick() {
        // Анализ больше не требуется
        statusLabel.setText("Анализ не требуется для работы с регулярными выражениями");
    }

    @FXML
    protected void runFlexBisonClick() {
        // Анализ больше не требуется
        statusLabel.setText("Анализ не требуется для работы с регулярными выражениями");
    }

    @FXML
    protected void runAntlrClick() {
        // Анализ больше не требуется
        statusLabel.setText("Анализ не требуется для работы с регулярными выражениями");
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

    @FXML
    protected void regexIdentifierClick() {
        analyzeRegex("identifier");
    }

    @FXML
    protected void regexUsernameClick() {
        analyzeRegex("username");
    }

    @FXML
    protected void regexLongitudeClick() {
        analyzeRegex("longitude");
    }

    private void analyzeRegex(String type) {
        String inputText = MultipleTabsService.getActiveCodeArea(tabPane).getText();

        // Очистка таблицы
        regexTable.getItems().clear();

        List<RegexMatchEntry> matches;
        String typeName;
        String pattern;

        switch (type) {
            case "identifier":
                matches = RegexAnalyzer.findIdentifiers(inputText);
                typeName = "Идентификатор";
                pattern = RegexAnalyzer.IDENTIFIER_PATTERN;
                break;
            case "username":
                matches = RegexAnalyzer.findUsernames(inputText);
                typeName = "Имя пользователя";
                pattern = RegexAnalyzer.USERNAME_PATTERN;
                break;
            case "longitude":
                matches = RegexAnalyzer.findLongitudes(inputText);
                typeName = "Долгота";
                pattern = RegexAnalyzer.LONGITUDE_PATTERN;
                break;
            default:
                matches = new ArrayList<>();
                typeName = "";
                pattern = "";
        }

        // Заполнение таблицы результатов
        regexTable.getItems().addAll(matches);
        regexTable.refresh();

        // Подсветка найденных совпадений в CodeArea
        CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
        HighlightingService.applyRegexHighlighting(activeCodeArea, null, pattern);

        // Обновление статуса
        int matchCount = matches.size();
        statusLabel.setText(String.format("%s: найдено совпадений: %d", typeName, matchCount));
    }

}
