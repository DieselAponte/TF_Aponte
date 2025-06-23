module com.example.javafx_aponte {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Abre los paquetes necesarios para FXML
    opens com.example.javafx_aponte to javafx.fxml;
    opens com.example.javafx_aponte.controllers to javafx.fxml;
    opens com.example.javafx_aponte.views to javafx.fxml;

    // Exporta los paquetes públicos
    exports com.example.javafx_aponte;
    exports com.example.javafx_aponte.controllers;
}