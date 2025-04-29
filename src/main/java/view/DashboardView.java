package view;

import com.example.client.Client;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DashboardView {
    public DashboardView(Stage stage, Client client, String role, String username) {
        if ("admin".equals(role)) {
            new AdminDashboardView(stage, client);
        } else if ("guest".equals(role)) {
            new GuestDashboardView(stage, client, username);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Неизвестная роль пользователя: " + role);
            alert.showAndWait();
            new LoginView(stage, client).show();
        }
    }
}