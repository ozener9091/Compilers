package scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class OutputEntry {

    private final StringProperty code = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty token = new SimpleStringProperty();
    private final StringProperty location = new SimpleStringProperty();

    public OutputEntry(String code, String type, String token, String location) {
        setCode(code);
        setType(type);
        setToken(token);
        setLocation(location);
    }

    public String getCode() { return code.get(); }
    public String getType() { return type.get(); }
    public String getToken() { return token.get(); }
    public String getLocation() { return location.get(); }

    public void setCode(String code) { this.code.set(code); }
    public void setType(String type) { this.type.set(type); }
    public void setToken(String token) { this.token.set(token); }
    public void setLocation(String location) { this.location.set(location); }

    public StringProperty codeProperty() { return code; }
    public StringProperty typeProperty() { return type; }
    public StringProperty tokenProperty() { return token; }
    public StringProperty locationProperty() { return location; }

}