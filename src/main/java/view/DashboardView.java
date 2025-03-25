package view;

import com.example.client.Client;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DashboardView {
    private final Stage stage;
    private final Client client;
    private final String role;

    public DashboardView(Stage stage, Client client, String role) {
        this.stage = stage;
        this.client = client;
        this.role = role;
    }

    public void show() {
        VBox dashboardPane = new VBox(20);
        dashboardPane.setAlignment(Pos.CENTER);
        dashboardPane.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Добро пожаловать в личный кабинет! Роль: " + role);
        welcomeLabel.setFont(new Font("Arial", 20));
        welcomeLabel.setTextFill(Color.WHITE);

        Button logoutButton = new Button("Выйти");
        logoutButton.setFont(new Font("Arial", 16));
        logoutButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle("-fx-background-color: #DD4444; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        dashboardPane.getChildren().addAll(welcomeLabel, logoutButton);

        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, client);
            loginView.show();
        });

        StackPane root = new StackPane(dashboardPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2D2D2D;");

        Scene dashboardScene = new Scene(root, 600, 600);
        stage.setScene(dashboardScene);
        stage.setTitle("Личный кабинет");
        stage.show();
    }
}