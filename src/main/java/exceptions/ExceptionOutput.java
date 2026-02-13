package exceptions;
import javafx.scene.control.TableView;

public class ExceptionOutput extends Throwable {

    private final TableView errorTable;

    public ExceptionOutput(TableView errorTable) {
        this.errorTable = errorTable;
    }

    public void ThrowException(String message){

    }
}



