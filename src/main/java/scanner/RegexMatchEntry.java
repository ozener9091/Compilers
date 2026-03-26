package scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Модель строки таблицы результатов поиска регулярных выражений.
 */
public class RegexMatchEntry {

    private final StringProperty match = new SimpleStringProperty();
    private final StringProperty position = new SimpleStringProperty();
    private final StringProperty length = new SimpleStringProperty();

    public RegexMatchEntry(String match, int startPos, int length) {
        setMatch(match);
        setPosition(startPos);
        setLength(length);
    }

    public String getMatch() { return match.get(); }
    public String getPosition() { return position.get(); }
    public String getLength() { return length.get(); }

    public void setMatch(String match) { this.match.set(match); }
    public void setPosition(int startPos) { this.position.set(String.valueOf(startPos)); }
    public void setLength(int length) { this.length.set(String.valueOf(length)); }

    public StringProperty matchProperty() { return match; }
    public StringProperty positionProperty() { return position; }
    public StringProperty lengthProperty() { return length; }
}
