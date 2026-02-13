package exceptions;

import javafx.scene.control.Label;

public class ExceptionOutput extends Throwable {

    private final Label errorLabel;

    public ExceptionOutput(Label errorLabel) {
        this.errorLabel = errorLabel;
    }

    public void ThrowException(String message){
        errorLabel.setText(errorLabel + "\n" + message);
    }
}



