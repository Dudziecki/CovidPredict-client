package view;

import com.example.client.Client;
import com.example.client.model.Response;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import view.DashboardView;

public class LoginView {
    private final Stage stage;
    private final Client client;

    public LoginView(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
    }

    public void show() {
        GridPane loginPane = new GridPane();
        loginPane.setHgap(15);
        loginPane.setVgap(15);
        loginPane.setPadding(new Insets(20));

        Label loginLabel = new Label("Логин:");
        loginLabel.setFont(new Font("Arial", 18));
        loginLabel.setTextFill(Color.WHITE);

        TextField loginField = new TextField();
        loginField.setPrefWidth(250);
        loginField.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10;");

        Label passwordLabel = new Label("Пароль:");
        passwordLabel.setFont(new Font("Arial", 18));
        passwordLabel.setTextFill(Color.WHITE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(250);
        passwordField.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10;");

        Button loginButton = new Button("Войти");
        loginButton.setFont(new Font("Arial", 16));
        loginButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #00A89A; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        Button registerButton = new Button("Регистрация");
        registerButton.setFont(new Font("Arial", 16));
        registerButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        registerButton.setOnMouseEntered(e -> registerButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        loginPane.add(loginLabel, 0, 0);
        loginPane.add(loginField, 1, 0);
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(passwordField, 1, 1);
        loginPane.add(loginButton, 1, 2);
        loginPane.add(registerButton, 1, 3);

        loginButton.setOnAction(e -> {
            try {
                String data = loginField.getText() + ":" + passwordField.getText();
                Response response = client.sendRequest("LOGIN", data);
                if (response.getMessage().startsWith("SUCCESS")) {
                    String role = response.getMessage().split(":")[1];
                    DashboardView dashboardView = new DashboardView(stage, client, role);
                    dashboardView.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Неверный логин или пароль!");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        registerButton.setOnAction(e -> {
           view.RegisterView registerView = new view.RegisterView(stage, client);
            registerView.show();
        });

        StackPane root = new StackPane(loginPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2D2D2D;");

        Scene loginScene = new Scene(root, 600, 600);
        stage.setScene(loginScene);
        stage.setTitle("Авторизация");
        stage.show();

        loginField.requestFocus();
    }
}