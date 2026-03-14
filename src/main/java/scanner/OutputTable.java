package scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OutputTable {

    public static void initOutputTable(
            TableColumn<OutputEntry, String> codeColumn,
            TableColumn<OutputEntry, String> typeColumn,
            TableColumn<OutputEntry, String> tokenColumn,
            TableColumn<OutputEntry, String> locationColumn,
            TableView<OutputEntry> outputTable) {

        codeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCode()));

        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        tokenColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getToken()));

        locationColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLocation()));

        codeColumn.prefWidthProperty().bind(outputTable.widthProperty().divide(3));
        typeColumn.prefWidthProperty().bind(outputTable.widthProperty().divide(3));
        tokenColumn.prefWidthProperty().bind(outputTable.widthProperty().divide(3));
        locationColumn.prefWidthProperty().bind(outputTable.widthProperty().divide(3));

    }
}