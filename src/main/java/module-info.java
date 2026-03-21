module com.example.compilers_laba {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.antlr.antlr4.runtime;

    opens com.example.compilers_laba1 to javafx.fxml;
    exports com.example.compilers_laba1;
    exports scanner;
    exports antlr;
}