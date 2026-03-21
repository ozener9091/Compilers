package exceptions;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ErrorTable {

    public static void initErrorTable(
            TableColumn<ErrorEntry, String> typeColumn,
            TableColumn<ErrorEntry, String> contentColumn,
            TableColumn<ErrorEntry, String> pageColumn,
            TableView<ErrorEntry> errorTable
    ) {
        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        pageColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPage()));

        contentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContent()));

        typeColumn.prefWidthProperty().bind(errorTable.widthProperty().multiply(0.25));
        pageColumn.prefWidthProperty().bind(errorTable.widthProperty().multiply(0.2));
        contentColumn.prefWidthProperty().bind(errorTable.widthProperty().multiply(0.55));
    }
}
