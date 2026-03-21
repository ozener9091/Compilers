package exceptions;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ErrorEntry {

    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final StringProperty page = new SimpleStringProperty();
    private final SimpleIntegerProperty line = new SimpleIntegerProperty();
    private final SimpleIntegerProperty column = new SimpleIntegerProperty();
    private final SimpleIntegerProperty length = new SimpleIntegerProperty();

    public ErrorEntry(String fragment, String description, String location) {
        this(fragment, description, location, parseLine(location), parseColumn(location), calculateLength(fragment));
    }

    public ErrorEntry(String fragment, String description, String location, int line, int column, int length) {
        setType(fragment);
        setContent(description);
        setPage(location);
        setLine(line);
        setColumn(column);
        setLength(length);
    }

    public String getType() {
        return type.get();
    }

    public String getContent() {
        return content.get();
    }

    public String getPage() {
        return page.get();
    }

    public int getLine() {
        return line.get();
    }

    public int getColumn() {
        return column.get();
    }

    public int getLength() {
        return length.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public void setPage(String page) {
        this.page.set(page);
    }

    public void setLine(int line) {
        this.line.set(line);
    }

    public void setColumn(int column) {
        this.column.set(column);
    }

    public void setLength(int length) {
        this.length.set(Math.max(length, 0));
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty contentProperty() {
        return content;
    }

    public StringProperty pageProperty() {
        return page;
    }

    private static int parseLine(String location) {
        if (location == null || !location.contains(":")) {
            return 1;
        }

        String[] parts = location.split(":");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private static int parseColumn(String location) {
        if (location == null || !location.contains(":")) {
            return 1;
        }

        String[] parts = location.split(":");
        if (parts.length < 2) {
            return 1;
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private static int calculateLength(String fragment) {
        if (fragment == null || fragment.isBlank() || "<конец ввода>".equals(fragment)) {
            return 0;
        }
        return fragment.length();
    }
}
