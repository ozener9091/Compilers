package exceptions;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ErrorTable{

    public static void initErrorTable(TableColumn<ErrorEntry, String> typeColumn,
                               TableColumn<ErrorEntry, String> contentColumn,
                               TableColumn<ErrorEntry, String> pageColumn,
                               TableView<ErrorEntry> errorTable) {

        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        contentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContent()));

        pageColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPage()));

        typeColumn.prefWidthProperty().bind(errorTable.widthProperty().divide(3));
        contentColumn.prefWidthProperty().bind(errorTable.widthProperty().divide(3));
        pageColumn.prefWidthProperty().bind(errorTable.widthProperty().divide(3));

        errorTable.getItems().addAll(
                new ErrorEntry("Ошибка синтаксиса", "Неожиданный символ ';'", "15"),
                new ErrorEntry("Предупреждение", "Неиспользуемая переменная 'x'", "23"),
                new ErrorEntry("Ошибка компиляции", "Метод не найден", "42")
        );
    }
}