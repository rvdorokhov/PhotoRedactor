module org.example.photoredactor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires opencv;
    requires jdk.jfr;
    requires java.logging;

    opens org.example.photoredactor to javafx.fxml;
    exports org.example.photoredactor;
    exports org.example.photoredactor.settings;
    opens org.example.photoredactor.settings to javafx.fxml;
}