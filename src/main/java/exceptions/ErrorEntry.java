package exceptions;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ErrorEntry {

    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final StringProperty page = new SimpleStringProperty();

    public ErrorEntry(String type, String content, String page) {
        setType(type);
        setContent(content);
        setPage(page);
    }

    public String getType() { return type.get(); }
    public String getContent() { return content.get(); }
    public String getPage() { return page.get(); }

    public void setType(String type) { this.type.set(type); }
    public void setContent(String content) { this.content.set(content); }
    public void setPage(String page) { this.page.set(page); }

    public StringProperty typeProperty() { return type; }
    public StringProperty contentProperty() { return content; }
    public StringProperty pageProperty() { return page; }
}