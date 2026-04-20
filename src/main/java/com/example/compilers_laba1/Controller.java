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
import parser.LambdaParser;
import parser.ast.AstTextPrinter;
import parser.ast.ProgramNode;
import parser.semantic.SemanticAnalyzer;
import scanner.*;
import exceptions.*;
import save.file.*;
import multipleTabsService.*;
import highlighting.HighlightingService;


enum Locale {
    English, Russian
}

public class Controller implements Initializable {

    //  Р“Р»Р°РІРЅРѕРµ РѕРєРЅРѕ
    @FXML
    private VBox mainWindow;

    //  РњРµРЅСЋ С„Р°Р№Р»
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

    //  РњРµРЅСЋ РїСЂР°РІРєР°
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

    //  РњРµРЅСЋ СЃРїСЂР°РІРєР°
    @FXML
    private Menu aboutLabel;
    @FXML
    private MenuItem userManualButton;
    @FXML
    private MenuItem aboutButton;

    //  РњРµРЅСЋ СЏР·С‹Рє
    @FXML
    private Menu languageLabel;
    @FXML
    private RadioMenuItem englishSelectButton;
    @FXML
    private RadioMenuItem russianSelectButton;

    //  РџСѓСЃРє
    @FXML
    private MenuItem runButton;
    @FXML
    private MenuItem runFlexBisonButton;
    @FXML
    private MenuItem runAntlrButton;

    //  РњРµРЅСЋ Р РµРіСѓР»СЏСЂРєРё
    @FXML
    private Menu regexLabel;
    @FXML
    private MenuItem regexIdentifierButton;
    @FXML
    private MenuItem regexUsernameButton;
    @FXML
    private MenuItem regexLongitudeButton;

    //  РњРµРЅСЋ Р’С‹РІРѕРґ
    @FXML
    private Menu outputLabel;

    //  РџР°РЅРµР»СЊ РёРЅСЃС‚СЂСѓРјРµРЅС‚РѕРІ
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

    //  РџРѕР»Рµ СЃРѕСЃС‚РѕСЏРЅРёСЏ
    @FXML
    private Label statusLabel;

    //  Р’РєР»Р°РґРєРё
    @FXML
    private TabPane tabPane;
    private final ObservableList<FileTab> fileTabs = FXCollections.observableArrayList();

    //  РњРµРЅСЋ РјРѕРґСѓР»РµР№
    @FXML
    private Menu analyzerLabel;
    @FXML
    private Menu pseudoCodeLabel;
    @FXML
    private Menu controlFlowGraphLabel;

    //  РўР°Р±Р»РёС†Р° СЃ СЂРµР·СѓР»СЊС‚Р°С‚Р°РјРё СЂРµРіСѓР»СЏСЂРЅС‹С… РІС‹СЂР°Р¶РµРЅРёР№
    @FXML
    private TableView<RegexMatchEntry> regexTable;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexMatchColumn;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexPositionColumn;
    @FXML
    private TableColumn<RegexMatchEntry, String> regexLengthColumn;
    @FXML
    private TextArea astOutputArea;
    @FXML
    private TextArea semanticOutputArea;
    @FXML
    private Label errorCountLabel;


    private Locale locale = Locale.Russian;
    private List<Object> localizationList = new ArrayList<>();
    private ExceptionOutput exceptionOutput;
    private Stage stage;
    private ProgramNode lastSemanticAst = new ProgramNode(List.of());

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
        astOutputArea.setText("AST empty.");
        semanticOutputArea.setText("Errors: none.");
        errorCountLabel.setText("РљРѕР»РёС‡РµСЃС‚РІРѕ РѕС€РёР±РѕРє: 0");
    }

    private void addAllToLocalizationList() {

        //  РњРµРЅСЋ
        localizationList.add(fileLabel);
        localizationList.add(editLabel);
        localizationList.add(aboutLabel);
        localizationList.add(languageLabel);

        //  РџР°РЅРµР»СЊ С„Р°Р№Р»
        localizationList.add(createButton);
        localizationList.add(loadFileButton);
        localizationList.add(saveButton);
        localizationList.add(saveAsButton);
        localizationList.add(exitButton);

        //  РњРµРЅСЋ РїСЂР°РІРєР°
        localizationList.add(undoButton);
        localizationList.add(returnButton);
        localizationList.add(cutButton);
        localizationList.add(copyButton);
        localizationList.add(pasteButton);
        localizationList.add(removeButton);
        localizationList.add(selectAllButton);

        //  РњРµРЅСЋ СЃРїСЂР°РІРєР°
        localizationList.add(userManualButton);
        localizationList.add(aboutButton);

        //  РњРµРЅСЋ СЏР·С‹Рє
        localizationList.add(englishSelectButton);
        localizationList.add(russianSelectButton);

        //  РџР°РЅРµР»СЊ РёРЅСЃС‚СЂСѓРјРµРЅС‚РѕРІ
        localizationList.add(createTooltip);
        localizationList.add(openTooltip);
        localizationList.add(saveTooltip);
        localizationList.add(undoTooltip);
        localizationList.add(returnTooltip);
        localizationList.add(copyTooltip);
        localizationList.add(cutTooltip);
        localizationList.add(pasteTooltip);

        //  РњРµРЅСЋ РџСѓСЃРє
        localizationList.add(runButton);
        localizationList.add(runFlexBisonButton);
        localizationList.add(runAntlrButton);

        //  РњРµРЅСЋ Р РµРіСѓР»СЏСЂРєРё
        localizationList.add(regexLabel);
        localizationList.add(regexIdentifierButton);
        localizationList.add(regexUsernameButton);
        localizationList.add(regexLongitudeButton);
    }

    private void getOutputService(){
        RegexOutputTable.initRegexTable(regexMatchColumn, regexPositionColumn, regexLengthColumn, regexTable);
    }

    private void initWindowStyle(){
        // РЎС‚РёР»Рё Р±РѕР»СЊС€Рµ РЅРµ С‚СЂРµР±СѓСЋС‚СЃСЏ
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
            position += lines[i].length() + 1; // +1 РґР»СЏ СЃРёРјРІРѕР»Р° РЅРѕРІРѕР№ СЃС‚СЂРѕРєРё
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
        fileChooser.setTitle("РћС‚РєСЂС‹С‚СЊ С„Р°Р№Р»");
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
                alert.setTitle("Рћ РїСЂРѕРіСЂР°РјРјРµ");
                alert.setHeaderText("Рћ РїСЂРѕРіСЂР°РјРјРµ");
                alert.setContentText("""
                Р›Р°Р±РѕСЂР°С‚РѕСЂРЅР°СЏ СЂР°Р±РѕС‚Р° в„–1
                РЎРґРµР»Р°Р»: РЎРёС‚РЅРёРєРѕРІ Р’.Р.
                Р“СЂСѓРїРїР°: РђРџ-326
                РџСЂРµРґРјРµС‚: РўРµРѕСЂРёСЏ С„РѕСЂРјР°Р»СЊРЅС‹С… СЏР·С‹РєРѕРІ Рё РєРѕРјРїРёР»СЏС‚РѕСЂРѕРІ
                РџСЂРѕРІРµСЂРёР»: РђРЅС‚РѕРЅСЏРЅС† Р•.Рќ.
                """);
            }
            case English -> {
                alert.setTitle("About program");
                alert.setHeaderText("About program");
                alert.setContentText("""
                Laboratory work в„–1
                Did: Sitnikov V.I.
                Group: AP-326
                Subject: Theory of formal languages and compilers
                Checked: Antonyants E.N.
                """);
            }
            default -> exceptionOutput.ThrowException("РћС€РёР±РєР° РїРѕРґРґРµСЂР¶РёРІР°РµРјРѕРіРѕ СЏР·С‹РєР°.");
        }
        alert.showAndWait();
    }

    @FXML
    protected void userManualClick() {
        Alert alert = new Alert(AlertType.INFORMATION);
        switch (locale) {
            case Russian -> {
                alert.setTitle("Р СѓРєРѕРІРѕРґСЃС‚РІРѕ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ");
                alert.setHeaderText("Р СѓРєРѕРІРѕРґСЃС‚РІРѕ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ");
            }
            case English -> {
                    alert.setTitle("User Manual");
                    alert.setHeaderText("User Manual");
            }
            default -> exceptionOutput.ThrowException("РћС€РёР±РєР° РїРѕРґРґРµСЂР¶РёРІР°РµРјРѕРіРѕ СЏР·С‹РєР°.");
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
                exceptionOutput.ThrowException("РћС€РёР±РєР° РѕС‚РєСЂС‹С‚РёСЏ СЃСЃС‹Р»РєРё.");
            }
        });
        alert.getDialogPane().setContent(linkLabel);
        alert.showAndWait();
    }

    @FXML
    protected void runClick() {
        CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
        if (activeCodeArea == null) {
            return;
        }
        String sourceCode = activeCodeArea.getText();
        Scanner.LexicalResult lexicalResult = Scanner.analyze(sourceCode);
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexicalResult.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());
        List<Scanner.ErrorInfo> allErrors = new ArrayList<>();
        allErrors.addAll(lexicalResult.getErrors());
        allErrors.addAll(parseResult.getErrors());
        allErrors.addAll(semanticResult.getErrors());
        lastSemanticAst = semanticResult.getAst();
        astOutputArea.setText(AstTextPrinter.print(lastSemanticAst));
        semanticOutputArea.setText(formatErrors(allErrors));
        errorCountLabel.setText("Количество ошибок: " + allErrors.size());
        if (allErrors.isEmpty()) {
            statusLabel.setText("Анализ завершен: ошибок нет.");
        } else {
            statusLabel.setText("Анализ завершен: ошибок " + allErrors.size() + ".");
        }
    }

    @FXML
    protected void runFlexBisonClick() {
        runClick();
    }

    @FXML
    protected void runAntlrClick() {
        runClick();
    }

    @FXML
    protected void showAstClick() {
        if (lastSemanticAst == null || lastSemanticAst.getDeclarations().isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("AST");
            alert.setHeaderText("AST отсутствует");
            alert.setContentText("Сначала выполните анализ корректной строки.");
            alert.showAndWait();
            return;
        }
        AstGraphWindow.show(lastSemanticAst);
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

    private String formatErrors(List<Scanner.ErrorInfo> errors) {
        if (errors == null || errors.isEmpty()) {
            return "Ошибок нет.";
        }

        StringBuilder builder = new StringBuilder();
        for (Scanner.ErrorInfo error : errors) {
            builder.append(error.getType())
                    .append(": ")
                    .append(error.getDescription())
                    .append(" (строка ")
                    .append(error.getLine())
                    .append(", символ ")
                    .append(error.getColumn())
                    .append(")")
                    .append('\n');
        }
        return builder.toString().trim();
    }

    private void analyzeRegex(String type) {
        String inputText = MultipleTabsService.getActiveCodeArea(tabPane).getText();

        // РћС‡РёСЃС‚РєР° С‚Р°Р±Р»РёС†С‹
        regexTable.getItems().clear();

        List<RegexMatchEntry> matches;
        String typeName;
        String pattern;

        switch (type) {
            case "identifier":
                matches = RegexAnalyzer.findIdentifiers(inputText);
                typeName = "РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ";
                pattern = RegexAnalyzer.IDENTIFIER_PATTERN;
                break;
            case "username":
                matches = RegexAnalyzer.findUsernames(inputText);
                typeName = "РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ";
                pattern = RegexAnalyzer.USERNAME_PATTERN;
                break;
            case "longitude":
                matches = RegexAnalyzer.findLongitudes(inputText);
                typeName = "Р”РѕР»РіРѕС‚Р°";
                pattern = RegexAnalyzer.LONGITUDE_PATTERN;
                break;
            default:
                matches = new ArrayList<>();
                typeName = "";
                pattern = "";
        }

        // Р—Р°РїРѕР»РЅРµРЅРёРµ С‚Р°Р±Р»РёС†С‹ СЂРµР·СѓР»СЊС‚Р°С‚РѕРІ
        regexTable.getItems().addAll(matches);
        regexTable.refresh();

        // РџРѕРґСЃРІРµС‚РєР° РЅР°Р№РґРµРЅРЅС‹С… СЃРѕРІРїР°РґРµРЅРёР№ РІ CodeArea
        CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
        HighlightingService.applyRegexHighlighting(activeCodeArea, null, pattern);

        // РћР±РЅРѕРІР»РµРЅРёРµ СЃС‚Р°С‚СѓСЃР°
        int matchCount = matches.size();
        statusLabel.setText(String.format("%s: РЅР°Р№РґРµРЅРѕ СЃРѕРІРїР°РґРµРЅРёР№: %d", typeName, matchCount));
    }

}

