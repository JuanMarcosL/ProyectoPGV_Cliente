module org.example.monitoreoservidores {
    requires javafx.controls;
    requires javafx.fxml;
    requires eu.hansolo.medusa;
    requires com.github.oshi;
    requires jasperreports;
    requires java.sql;
    requires java.desktop;


    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.controller;
    opens org.example.controller to javafx.fxml;
}