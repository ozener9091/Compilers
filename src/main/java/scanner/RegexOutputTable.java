package scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Инициализация таблицы результатов поиска регулярных выражений.
 */
public class RegexOutputTable {

    public static void initRegexTable(
            TableColumn<RegexMatchEntry, String> matchColumn,
            TableColumn<RegexMatchEntry, String> positionColumn,
            TableColumn<RegexMatchEntry, String> lengthColumn,
            TableView<RegexMatchEntry> regexTable) {

        matchColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMatch()));

        positionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPosition()));

        lengthColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLength()));

        matchColumn.prefWidthProperty().bind(regexTable.widthProperty().multiply(0.5));
        positionColumn.prefWidthProperty().bind(regexTable.widthProperty().multiply(0.25));
        lengthColumn.prefWidthProperty().bind(regexTable.widthProperty().multiply(0.25));
    }
}
