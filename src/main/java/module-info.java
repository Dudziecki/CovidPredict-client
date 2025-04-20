module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports view;
    opens view to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.example.client.model to com.fasterxml.jackson.databind;
}
