package com.example.compilers_laba1;

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
import javafx.stage.Stage;
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

import hotkeysService.*;
import localization.*;
import org.fxmisc.richtext.CodeArea;
import parser.LambdaParser;
import scanner.*;
import exceptions.*;
import save.file.*;
import multipleTabsService.*;


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

    //  Поле вывода
    @FXML
    private TableView<OutputEntry> outputTable;
    @FXML
    private TableColumn<OutputEntry, String> codeColumn;
    @FXML
    private TableColumn<OutputEntry, String> tokenTypeColumn;
    @FXML
    private TableColumn<OutputEntry, String> tokenColumn;
    @FXML
    private TableColumn<OutputEntry, String> locationColumn;

    //  Таблица с результатами регулярных выражений
    @FXML
    private TableView<RegexMatchEntry> regexTable;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexMatchColumn;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexPositionColumn;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexLengthColumn;

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
    private List<ErrorEntry> currentErrors = new ArrayList<>();
    private List<Scanner.ErrorInfo> lexicalErrors = new ArrayList<>();
    private double outputCurrentFontSize = 14;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (tabPane.getTabs().isEmpty()) {
            MultipleTabsService.createNewTab(tabPane, fileTabs, null, statusLabel);
        }

        initWindowStyle();
        addAllToLocalizationList();
        getErrorService();
        getOutputService();
        initHotkeys();
        initErrorNavigation();
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

        //  Меню вывода
        localizationList.add(analyzerLabel);
        localizationList.add(pseudoCodeLabel);
        localizationList.add(controlFlowGraphLabel);

        //  Меню Пуск
        localizationList.add(runButton);
        localizationList.add(runFlexBisonButton);
        localizationList.add(runAntlrButton);

        //  Меню Регулярки
        localizationList.add(regexLabel);
        localizationList.add(regexIdentifierButton);
        localizationList.add(regexUsernameButton);
        localizationList.add(regexLongitudeButton);

        //  Меню Вывод
        localizationList.add(outputLabel);

        //  Таблица с ошибками
        localizationList.add(typeColumn);
        localizationList.add(contentColumn);
        localizationList.add(pageColumn);
    }

    private void getErrorService(){
        exceptionOutput = new ExceptionOutput(errorTable);
        ErrorTable.initErrorTable(typeColumn, contentColumn, pageColumn, errorTable);
    }

    private void getOutputService(){
        OutputTable.initOutputTable(codeColumn, tokenTypeColumn, tokenColumn, locationColumn, outputTable);
        RegexOutputTable.initRegexTable(regexMatchColumn, regexPositionColumn, regexLengthColumn, regexTable);
    }

    private void initWindowStyle(){
        outputTable.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
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

    private void initErrorNavigation() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem goToErrorItem = new MenuItem("Перейти к ошибке");
        goToErrorItem.setOnAction(event -> {
            ErrorEntry selectedError = errorTable.getSelectionModel().getSelectedItem();
            if (selectedError != null) {
                navigateToError(selectedError);
            }
        });
        contextMenu.getItems().add(goToErrorItem);
        errorTable.setContextMenu(contextMenu);

        errorTable.setRowFactory(tv -> {
            TableRow<ErrorEntry> row = new TableRow<>();
            row.hoverProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && !row.isEmpty()) {
                    row.setStyle("-fx-background-color: #ffcccc;");
                } else if (!row.isEmpty()) {
                    row.setStyle("");
                }
            });

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    ErrorEntry errorEntry = row.getItem();
                    navigateToError(errorEntry);
                }
            });

            return row;
        });
    }

    private void navigateToError(ErrorEntry errorEntry) {
        if (errorEntry == null || errorEntry.getPage() == null) return;

        try {
            String page = errorEntry.getPage();
            if (page == null || page.isEmpty()) return;

            String[] position = page.split(":");
            if (position.length < 2) return;

            int line = Integer.parseInt(position[0]);
            int column = Integer.parseInt(position[1]);

            CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
            if (activeCodeArea == null) return;

            int caretPosition = getPositionFromLineAndColumn(activeCodeArea.getText(), line, column);

            activeCodeArea.moveTo(caretPosition);
            activeCodeArea.requestFocus();

            highlightErrorToken(activeCodeArea, errorEntry.getContent(), caretPosition);

            statusLabel.setText("Переход к ошибке: " + errorEntry.getContent());

        } catch (NumberFormatException e) {
            exceptionOutput.ThrowException("Ошибка при парсинге позиции ошибки");
        }
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

    private void highlightErrorToken(CodeArea codeArea, String errorContent, int caretPosition) {
        String token = extractTokenFromError(errorContent);
        if (token != null && !token.isEmpty()) {
            codeArea.selectRange(caretPosition, caretPosition + token.length());
        } else {
            codeArea.selectRange(caretPosition, caretPosition);
        }
    }

    private String extractTokenFromError(String errorContent) {
        if (errorContent == null) return null;

        int startQuote = errorContent.indexOf('\'');
        int endQuote = errorContent.lastIndexOf('\'');

        if (startQuote != -1 && endQuote != -1 && startQuote < endQuote) {
            return errorContent.substring(startQuote + 1, endQuote);
        }

        return null;
    }

    private void highlightAllErrors(CodeArea codeArea, List<Scanner.ErrorInfo> errors) {
        codeArea.setStyleSpans(0, codeArea.getStyleSpans(0, codeArea.getLength()));

        for (Scanner.ErrorInfo error : errors) {
            try {
                String[] position = error.getPage().split(":");
                if (position.length < 2) continue;

                int line = Integer.parseInt(position[0]);
                int column = Integer.parseInt(position[1]);

                int startPos = getPositionFromLineAndColumn(codeArea.getText(), line, column);
                String token = extractTokenFromError(error.getContent());

                if (token != null && !token.isEmpty()) {
                    int endPos = startPos + token.length();
                }
            } catch (Exception e) {
            }
        }
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
        String inputText = MultipleTabsService.getActiveCodeArea(tabPane).getText();

        outputTable.getItems().clear();
        errorTable.getItems().clear();
        currentErrors.clear();

        Scanner.LexicalResult lexicalResult = Scanner.analyze(inputText);
        List<Scanner.TokenInfo> tokens = lexicalResult.getTokens();
        lexicalErrors = lexicalResult.getErrors();

        for (Scanner.TokenInfo tokenInfo : tokens) {
            outputTable.getItems().add(new OutputEntry(
                    tokenInfo.getCode(),
                    tokenInfo.getTokenType(),
                    tokenInfo.getToken(),
                    tokenInfo.getLocation()
            ));
        }

        for (Scanner.ErrorInfo errorInfo : lexicalErrors) {
            currentErrors.add(new ErrorEntry(
                    errorInfo.getFragment(),
                    errorInfo.getDescription(),
                    errorInfo.getLocation(),
                    errorInfo.getLine(),
                    errorInfo.getColumn(),
                    errorInfo.getHighlightLength()
            ));
        }

        if (lexicalErrors.isEmpty()) {
            LambdaParser.ParseResult parseResult = LambdaParser.parse(lexicalResult.getLexemes());
            for (Scanner.ErrorInfo errorInfo : parseResult.getErrors()) {
                currentErrors.add(new ErrorEntry(
                        errorInfo.getFragment(),
                        errorInfo.getDescription(),
                        errorInfo.getLocation(),
                        errorInfo.getLine(),
                        errorInfo.getColumn(),
                        errorInfo.getHighlightLength()
                ));
            }
        }

        errorTable.getItems().setAll(currentErrors);
        outputTable.refresh();
        errorTable.refresh();

        if (currentErrors.isEmpty()) {
            statusLabel.setText("Синтаксический анализ завершен. Ошибок не найдено.");
        } else {
            statusLabel.setText(!lexicalErrors.isEmpty()
                    ? "Найдено ошибок: " + currentErrors.size() + ". Сначала исправьте лексические ошибки."
                    : "Найдено ошибок: " + currentErrors.size() + ". Выберите строку в таблице для перехода.");
        }

        CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
        if (activeCodeArea != null && !currentErrors.isEmpty()) {
            navigateToError(currentErrors.getFirst());
        }

    }

    @FXML
    protected void runFlexBisonClick() {
        String inputText = MultipleTabsService.getActiveCodeArea(tabPane).getText();

        outputTable.getItems().clear();
        errorTable.getItems().clear();
        currentErrors.clear();

        try {
            FlexAdapter.Result result = FlexAdapter.parse(inputText);

            for (Scanner.TokenInfo tokenInfo : result.getTokens()) {
                outputTable.getItems().add(new OutputEntry(
                        tokenInfo.getCode(),
                        tokenInfo.getTokenType(),
                        tokenInfo.getToken(),
                        tokenInfo.getLocation()
                ));
            }

            for (Scanner.ErrorInfo errorInfo : result.getErrors()) {
                currentErrors.add(new ErrorEntry(
                        errorInfo.getFragment(),
                        errorInfo.getDescription(),
                        errorInfo.getLocation(),
                        errorInfo.getLine(),
                        errorInfo.getColumn(),
                        errorInfo.getHighlightLength()
                ));
            }

            errorTable.getItems().setAll(currentErrors);
            outputTable.refresh();
            errorTable.refresh();

            statusLabel.setText("Flex/Bison анализ завершен. Токенов: " + result.getTokens().size() + ", ошибок: " + currentErrors.size());

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка при запуске внешнего анализатора: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось запустить внешний анализатор");
            alert.setContentText("Убедитесь, что файл parser.exe находится в папке приложения.\n" +
                    "Текущая рабочая директория: " + System.getProperty("user.dir"));
            alert.showAndWait();
        }
    }

    @FXML
    protected void runAntlrClick() {
        String inputText = MultipleTabsService.getActiveCodeArea(tabPane).getText();

        outputTable.getItems().clear();
        errorTable.getItems().clear();
        currentErrors.clear();

        try {
            AntlrAdapter.AntlrResult result = AntlrAdapter.analyze(inputText);

            for (Scanner.TokenInfo tokenInfo : result.getTokens()) {
                outputTable.getItems().add(new OutputEntry(
                        tokenInfo.getCode(),
                        tokenInfo.getTokenType(),
                        tokenInfo.getToken(),
                        tokenInfo.getLocation()
                ));
            }

            for (Scanner.ErrorInfo errorInfo : result.getAllErrors()) {
                currentErrors.add(new ErrorEntry(
                        errorInfo.getFragment(),
                        errorInfo.getDescription(),
                        errorInfo.getLocation(),
                        errorInfo.getLine(),
                        errorInfo.getColumn(),
                        errorInfo.getHighlightLength()
                ));
            }

            errorTable.getItems().setAll(currentErrors);
            outputTable.refresh();
            errorTable.refresh();

            if (currentErrors.isEmpty()) {
                statusLabel.setText("ANTLR анализ завершен. Токенов: " + result.getTokens().size() + ", ошибок не найдено.");
            } else {
                statusLabel.setText("ANTLR анализ завершен. Токенов: " + result.getTokens().size() + ", ошибок: " + currentErrors.size());
            }

            if (!currentErrors.isEmpty()) {
                navigateToError(currentErrors.getFirst());
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка при запуске ANTLR анализатора: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось запустить ANTLR анализатор");
            alert.setContentText("Произошла ошибка при выполнении анализа: " + e.getMessage());
            alert.showAndWait();
        }
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
        outputTable.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
    }

    @FXML
    protected  void decreaseOutputClick() {
        if (outputCurrentFontSize <= 14) return;
        outputCurrentFontSize -= 2;
        outputTable.setStyle("-fx-font-size: " + outputCurrentFontSize + "px; -fx-font-family: 'Monospaced'; -fx-padding: 10;");
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

        // Очистка таблиц
        regexTable.getItems().clear();
        outputTable.getItems().clear();

        List<RegexMatchEntry> matches;
        String typeName;

        switch (type) {
            case "identifier":
                matches = RegexAnalyzer.findIdentifiers(inputText);
                typeName = "Идентификатор";
                break;
            case "username":
                matches = RegexAnalyzer.findUsernames(inputText);
                typeName = "Имя пользователя";
                break;
            case "longitude":
                matches = RegexAnalyzer.findLongitudes(inputText);
                typeName = "Долгота";
                break;
            default:
                matches = new ArrayList<>();
                typeName = "";
        }

        // Заполнение таблицы результатов
        regexTable.getItems().addAll(matches);
        regexTable.refresh();

        // Подсветка найденных совпадений в CodeArea
        CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
        highlightRegexMatches(activeCodeArea, matches);

        // Обновление статуса
        int matchCount = matches.size();
        statusLabel.setText(String.format("%s: найдено совпадений: %d", typeName, matchCount));
    }

    private void highlightRegexMatches(CodeArea codeArea, List<RegexMatchEntry> matches) {
        // Сброс всех стилей
        codeArea.clearStyle(0, codeArea.getLength());

        // Применение подсветки для каждого совпадения
        for (RegexMatchEntry match : matches) {
            int start = Integer.parseInt(match.getPosition());
            int length = Integer.parseInt(match.getLength());
            // Подсветка жёлтым фоном
            codeArea.setStyle(start, start + length, java.util.Set.of("-fx-background-color: #ffff00; -fx-font-weight: bold;"));
        }
    }

}
