module com.example.javafx_aponte {
    // Dependencias de JavaFX y JDBC
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Permitir a FXMLLoader acceso a los controladores y la clase principal
    opens com.example.javafx_aponte.controllers to javafx.fxml;
    opens com.example.javafx_aponte to javafx.fxml;

    // Si tus modelos usan binding con FXML (p. ej. propiedades), ábrelos también:
    opens com.example.javafx_aponte.models to javafx.fxml;

    // Exporta solo aquello que podría usarse desde otros módulos (opcional)
    exports com.example.javafx_aponte;
    exports com.example.javafx_aponte.controllers;
}
