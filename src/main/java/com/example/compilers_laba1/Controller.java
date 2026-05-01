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
import parser.ir.IntermediateCodeGenerator;
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

    //  Р вЂњР В»Р В°Р Р†Р Р…Р С•Р Вµ Р С•Р С”Р Р…Р С•
    @FXML
    private VBox mainWindow;

    //  Р СљР ВµР Р…РЎР‹ РЎвЂћР В°Р в„–Р В»
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

    //  Р СљР ВµР Р…РЎР‹ Р С—РЎР‚Р В°Р Р†Р С”Р В°
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

    //  Р СљР ВµР Р…РЎР‹ РЎРѓР С—РЎР‚Р В°Р Р†Р С”Р В°
    @FXML
    private Menu aboutLabel;
    @FXML
    private MenuItem userManualButton;
    @FXML
    private MenuItem aboutButton;

    //  Р СљР ВµР Р…РЎР‹ РЎРЏР В·РЎвЂ№Р С”
    @FXML
    private Menu languageLabel;
    @FXML
    private RadioMenuItem englishSelectButton;
    @FXML
    private RadioMenuItem russianSelectButton;

    //  Р СџРЎС“РЎРѓР С”
    @FXML
    private MenuItem runButton;
    @FXML
    private MenuItem runFlexBisonButton;
    @FXML
    private MenuItem runAntlrButton;

    //  Р СљР ВµР Р…РЎР‹ Р В Р ВµР С–РЎС“Р В»РЎРЏРЎР‚Р С”Р С‘
    @FXML
    private Menu regexLabel;
    @FXML
    private MenuItem regexIdentifierButton;
    @FXML
    private MenuItem regexUsernameButton;
    @FXML
    private MenuItem regexLongitudeButton;

    //  Р СљР ВµР Р…РЎР‹ Р вЂ™РЎвЂ№Р Р†Р С•Р Т‘
    @FXML
    private Menu outputLabel;

    //  Р СџР В°Р Р…Р ВµР В»РЎРЉ Р С‘Р Р…РЎРѓРЎвЂљРЎР‚РЎС“Р СР ВµР Р…РЎвЂљР С•Р Р†
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

    //  Р СџР С•Р В»Р Вµ РЎРѓР С•РЎРѓРЎвЂљР С•РЎРЏР Р…Р С‘РЎРЏ
    @FXML
    private Label statusLabel;

    //  Р вЂ™Р С”Р В»Р В°Р Т‘Р С”Р С‘
    @FXML
    private TabPane tabPane;
    private final ObservableList<FileTab> fileTabs = FXCollections.observableArrayList();

    //  Р СљР ВµР Р…РЎР‹ Р СР С•Р Т‘РЎС“Р В»Р ВµР в„–
    @FXML
    private Menu analyzerLabel;
    @FXML
    private Menu pseudoCodeLabel;
    @FXML
    private Menu controlFlowGraphLabel;

    //  Р СћР В°Р В±Р В»Р С‘РЎвЂ Р В° РЎРѓ РЎР‚Р ВµР В·РЎС“Р В»РЎРЉРЎвЂљР В°РЎвЂљР В°Р СР С‘ РЎР‚Р ВµР С–РЎС“Р В»РЎРЏРЎР‚Р Р…РЎвЂ№РЎвЂ¦ Р Р†РЎвЂ№РЎР‚Р В°Р В¶Р ВµР Р…Р С‘Р в„–
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
        errorCountLabel.setText("Р С™Р С•Р В»Р С‘РЎвЂЎР ВµРЎРѓРЎвЂљР Р†Р С• Р С•РЎв‚¬Р С‘Р В±Р С•Р С”: 0");
    }

    private void addAllToLocalizationList() {

        //  Р СљР ВµР Р…РЎР‹
        localizationList.add(fileLabel);
        localizationList.add(editLabel);
        localizationList.add(aboutLabel);
        localizationList.add(languageLabel);

        //  Р СџР В°Р Р…Р ВµР В»РЎРЉ РЎвЂћР В°Р в„–Р В»
        localizationList.add(createButton);
        localizationList.add(loadFileButton);
        localizationList.add(saveButton);
        localizationList.add(saveAsButton);
        localizationList.add(exitButton);

        //  Р СљР ВµР Р…РЎР‹ Р С—РЎР‚Р В°Р Р†Р С”Р В°
        localizationList.add(undoButton);
        localizationList.add(returnButton);
        localizationList.add(cutButton);
        localizationList.add(copyButton);
        localizationList.add(pasteButton);
        localizationList.add(removeButton);
        localizationList.add(selectAllButton);

        //  Р СљР ВµР Р…РЎР‹ РЎРѓР С—РЎР‚Р В°Р Р†Р С”Р В°
        localizationList.add(userManualButton);
        localizationList.add(aboutButton);

        //  Р СљР ВµР Р…РЎР‹ РЎРЏР В·РЎвЂ№Р С”
        localizationList.add(englishSelectButton);
        localizationList.add(russianSelectButton);

        //  Р СџР В°Р Р…Р ВµР В»РЎРЉ Р С‘Р Р…РЎРѓРЎвЂљРЎР‚РЎС“Р СР ВµР Р…РЎвЂљР С•Р Р†
        localizationList.add(createTooltip);
        localizationList.add(openTooltip);
        localizationList.add(saveTooltip);
        localizationList.add(undoTooltip);
        localizationList.add(returnTooltip);
        localizationList.add(copyTooltip);
        localizationList.add(cutTooltip);
        localizationList.add(pasteTooltip);

        //  Р СљР ВµР Р…РЎР‹ Р СџРЎС“РЎРѓР С”
        localizationList.add(runButton);
        localizationList.add(runFlexBisonButton);
        localizationList.add(runAntlrButton);

        //  Р СљР ВµР Р…РЎР‹ Р В Р ВµР С–РЎС“Р В»РЎРЏРЎР‚Р С”Р С‘
        localizationList.add(regexLabel);
        localizationList.add(regexIdentifierButton);
        localizationList.add(regexUsernameButton);
        localizationList.add(regexLongitudeButton);
    }

    private void getOutputService(){
        RegexOutputTable.initRegexTable(regexMatchColumn, regexPositionColumn, regexLengthColumn, regexTable);
    }

    private void initWindowStyle(){
        // Р РЋРЎвЂљР С‘Р В»Р С‘ Р В±Р С•Р В»РЎРЉРЎв‚¬Р Вµ Р Р…Р Вµ РЎвЂљРЎР‚Р ВµР В±РЎС“РЎР‹РЎвЂљРЎРѓРЎРЏ
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
            position += lines[i].length() + 1; // +1 Р Т‘Р В»РЎРЏ РЎРѓР С‘Р СР Р†Р С•Р В»Р В° Р Р…Р С•Р Р†Р С•Р в„– РЎРѓРЎвЂљРЎР‚Р С•Р С”Р С‘
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
        fileChooser.setTitle("Р С›РЎвЂљР С”РЎР‚РЎвЂ№РЎвЂљРЎРЉ РЎвЂћР В°Р в„–Р В»");
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
                alert.setTitle("Р С› Р С—РЎР‚Р С•Р С–РЎР‚Р В°Р СР СР Вµ");
                alert.setHeaderText("Р С› Р С—РЎР‚Р С•Р С–РЎР‚Р В°Р СР СР Вµ");
                alert.setContentText("""
                Р вЂєР В°Р В±Р С•РЎР‚Р В°РЎвЂљР С•РЎР‚Р Р…Р В°РЎРЏ РЎР‚Р В°Р В±Р С•РЎвЂљР В° РІвЂћвЂ“1
                Р РЋР Т‘Р ВµР В»Р В°Р В»: Р РЋР С‘РЎвЂљР Р…Р С‘Р С”Р С•Р Р† Р вЂ™.Р В.
                Р вЂњРЎР‚РЎС“Р С—Р С—Р В°: Р С’Р Сџ-326
                Р СџРЎР‚Р ВµР Т‘Р СР ВµРЎвЂљ: Р СћР ВµР С•РЎР‚Р С‘РЎРЏ РЎвЂћР С•РЎР‚Р СР В°Р В»РЎРЉР Р…РЎвЂ№РЎвЂ¦ РЎРЏР В·РЎвЂ№Р С”Р С•Р Р† Р С‘ Р С”Р С•Р СР С—Р С‘Р В»РЎРЏРЎвЂљР С•РЎР‚Р С•Р Р†
                Р СџРЎР‚Р С•Р Р†Р ВµРЎР‚Р С‘Р В»: Р С’Р Р…РЎвЂљР С•Р Р…РЎРЏР Р…РЎвЂ  Р вЂў.Р Сњ.
                """);
            }
            case English -> {
                alert.setTitle("About program");
                alert.setHeaderText("About program");
                alert.setContentText("""
                Laboratory work РІвЂћвЂ“1
                Did: Sitnikov V.I.
                Group: AP-326
                Subject: Theory of formal languages and compilers
                Checked: Antonyants E.N.
                """);
            }
            default -> exceptionOutput.ThrowException("Р С›РЎв‚¬Р С‘Р В±Р С”Р В° Р С—Р С•Р Т‘Р Т‘Р ВµРЎР‚Р В¶Р С‘Р Р†Р В°Р ВµР СР С•Р С–Р С• РЎРЏР В·РЎвЂ№Р С”Р В°.");
        }
        alert.showAndWait();
    }

    @FXML
    protected void userManualClick() {
        Alert alert = new Alert(AlertType.INFORMATION);
        switch (locale) {
            case Russian -> {
                alert.setTitle("Р В РЎС“Р С”Р С•Р Р†Р С•Р Т‘РЎРѓРЎвЂљР Р†Р С• Р С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°РЎвЂљР ВµР В»РЎРЏ");
                alert.setHeaderText("Р В РЎС“Р С”Р С•Р Р†Р С•Р Т‘РЎРѓРЎвЂљР Р†Р С• Р С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°РЎвЂљР ВµР В»РЎРЏ");
            }
            case English -> {
                    alert.setTitle("User Manual");
                    alert.setHeaderText("User Manual");
            }
            default -> exceptionOutput.ThrowException("Р С›РЎв‚¬Р С‘Р В±Р С”Р В° Р С—Р С•Р Т‘Р Т‘Р ВµРЎР‚Р В¶Р С‘Р Р†Р В°Р ВµР СР С•Р С–Р С• РЎРЏР В·РЎвЂ№Р С”Р В°.");
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
                exceptionOutput.ThrowException("Р С›РЎв‚¬Р С‘Р В±Р С”Р В° Р С•РЎвЂљР С”РЎР‚РЎвЂ№РЎвЂљР С‘РЎРЏ РЎРѓРЎРѓРЎвЂ№Р В»Р С”Р С‘.");
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

        boolean hasLexicalOrSyntaxErrors = !lexicalResult.getErrors().isEmpty() || !parseResult.getErrors().isEmpty();
        SemanticAnalyzer.SemanticResult semanticResult = hasLexicalOrSyntaxErrors
                ? new SemanticAnalyzer.SemanticResult(new ProgramNode(List.of()), List.of())
                : SemanticAnalyzer.analyze(parseResult.getAst());

        List<Scanner.ErrorInfo> allErrors = new ArrayList<>();
        allErrors.addAll(lexicalResult.getErrors());
        allErrors.addAll(parseResult.getErrors());
        allErrors.addAll(semanticResult.getErrors());

        lastSemanticAst = semanticResult.getAst();
        IntermediateCodeGenerator.ProgramIrResult irResult = hasLexicalOrSyntaxErrors
                ? new IntermediateCodeGenerator.ProgramIrResult(List.of())
                : IntermediateCodeGenerator.build(parseResult.getAst());

        astOutputArea.setText(formatAstAndIr(lastSemanticAst, irResult, hasLexicalOrSyntaxErrors));
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
            alert.setHeaderText("AST РѕС‚СЃСѓС‚СЃС‚РІСѓРµС‚");
            alert.setContentText("РЎРЅР°С‡Р°Р»Р° РІС‹РїРѕР»РЅРёС‚Рµ Р°РЅР°Р»РёР· РєРѕСЂСЂРµРєС‚РЅРѕР№ СЃС‚СЂРѕРєРё.");
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
            return "РћС€РёР±РѕРє РЅРµС‚.";
        }

        StringBuilder builder = new StringBuilder();
        for (Scanner.ErrorInfo error : errors) {
            builder.append(error.getType())
                    .append(": ")
                    .append(error.getDescription())
                    .append(" (СЃС‚СЂРѕРєР° ")
                    .append(error.getLine())
                    .append(", СЃРёРјРІРѕР» ")
                    .append(error.getColumn())
                    .append(")")
                    .append('\n');
        }
        return builder.toString().trim();
    }

    private String formatAstAndIr(
            ProgramNode semanticAst,
            IntermediateCodeGenerator.ProgramIrResult irResult,
            boolean blockedByLexicalOrSyntaxErrors
    ) {
        StringBuilder builder = new StringBuilder();

        builder.append("AST").append('\n');
        builder.append(AstTextPrinter.print(semanticAst)).append('\n').append('\n');

        builder.append("Тетрады").append('\n');
        if (blockedByLexicalOrSyntaxErrors) {
            builder.append("Построение тетрад пропущено: есть лексические или синтаксические ошибки.").append('\n');
        } else if (irResult.getDeclarations().isEmpty()) {
            builder.append("Нет корректных выражений для построения тетрад.").append('\n');
        } else {
            for (IntermediateCodeGenerator.LambdaIrResult declaration : irResult.getDeclarations()) {
                builder.append("Функция ").append(declaration.getDeclarationName()).append(":").append('\n');

                if (declaration.getQuadruples().isEmpty()) {
                    builder.append("  (тетрады не требуются: выражение состоит из одного операнда)").append('\n');
                } else {
                    for (IntermediateCodeGenerator.Quadruple quadruple : declaration.getQuadruples()) {
                        builder.append("  ")
                                .append(quadruple.getIndex())
                                .append(": (")
                                .append(quadruple.getOp()).append(", ")
                                .append(quadruple.getArg1()).append(", ")
                                .append(quadruple.getArg2()).append(", ")
                                .append(quadruple.getResult())
                                .append(")")
                                .append('\n');
                    }
                    builder.append("  result = ").append(declaration.getFinalResult()).append('\n');
                }
            }
        }

        builder.append('\n').append("ПОЛИЗ").append('\n');
        if (blockedByLexicalOrSyntaxErrors) {
            builder.append("Построение ПОЛИЗ пропущено: есть лексические или синтаксические ошибки.");
        } else if (irResult.getDeclarations().isEmpty()) {
            builder.append("Нет корректных выражений для построения ПОЛИЗ.");
        } else {
            for (IntermediateCodeGenerator.LambdaIrResult declaration : irResult.getDeclarations()) {
                builder.append("Функция ").append(declaration.getDeclarationName()).append(":").append('\n');
                builder.append("  ").append(String.join(" ", declaration.getPoliz())).append('\n');
                if (declaration.getEvaluatedValue() != null) {
                    builder.append("  value = ").append(declaration.getEvaluatedValue()).append('\n');
                } else {
                    builder.append("  ").append(declaration.getEvaluationError()).append('\n');
                }
            }
        }

        return builder.toString().trim();
    }
    private void analyzeRegex(String type) {
        String inputText = MultipleTabsService.getActiveCodeArea(tabPane).getText();

        // Р С›РЎвЂЎР С‘РЎРѓРЎвЂљР С”Р В° РЎвЂљР В°Р В±Р В»Р С‘РЎвЂ РЎвЂ№
        regexTable.getItems().clear();

        List<RegexMatchEntry> matches;
        String typeName;
        String pattern;

        switch (type) {
            case "identifier":
                matches = RegexAnalyzer.findIdentifiers(inputText);
                typeName = "Р ВР Т‘Р ВµР Р…РЎвЂљР С‘РЎвЂћР С‘Р С”Р В°РЎвЂљР С•РЎР‚";
                pattern = RegexAnalyzer.IDENTIFIER_PATTERN;
                break;
            case "username":
                matches = RegexAnalyzer.findUsernames(inputText);
                typeName = "Р ВР СРЎРЏ Р С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°РЎвЂљР ВµР В»РЎРЏ";
                pattern = RegexAnalyzer.USERNAME_PATTERN;
                break;
            case "longitude":
                matches = RegexAnalyzer.findLongitudes(inputText);
                typeName = "Р вЂќР С•Р В»Р С–Р С•РЎвЂљР В°";
                pattern = RegexAnalyzer.LONGITUDE_PATTERN;
                break;
            default:
                matches = new ArrayList<>();
                typeName = "";
                pattern = "";
        }

        // Р вЂ”Р В°Р С—Р С•Р В»Р Р…Р ВµР Р…Р С‘Р Вµ РЎвЂљР В°Р В±Р В»Р С‘РЎвЂ РЎвЂ№ РЎР‚Р ВµР В·РЎС“Р В»РЎРЉРЎвЂљР В°РЎвЂљР С•Р Р†
        regexTable.getItems().addAll(matches);
        regexTable.refresh();

        // Р СџР С•Р Т‘РЎРѓР Р†Р ВµРЎвЂљР С”Р В° Р Р…Р В°Р в„–Р Т‘Р ВµР Р…Р Р…РЎвЂ№РЎвЂ¦ РЎРѓР С•Р Р†Р С—Р В°Р Т‘Р ВµР Р…Р С‘Р в„– Р Р† CodeArea
        CodeArea activeCodeArea = MultipleTabsService.getActiveCodeArea(tabPane);
        HighlightingService.applyRegexHighlighting(activeCodeArea, null, pattern);

        // Р С›Р В±Р Р…Р С•Р Р†Р В»Р ВµР Р…Р С‘Р Вµ РЎРѓРЎвЂљР В°РЎвЂљРЎС“РЎРѓР В°
        int matchCount = matches.size();
        statusLabel.setText(String.format("%s: Р Р…Р В°Р в„–Р Т‘Р ВµР Р…Р С• РЎРѓР С•Р Р†Р С—Р В°Р Т‘Р ВµР Р…Р С‘Р в„–: %d", typeName, matchCount));
    }

}



