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

public class RegisterView {
    private final Stage stage;
    private final Client client;

    public RegisterView(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
    }

    public void show() {
        GridPane registerPane = new GridPane();
        registerPane.setHgap(15);
        registerPane.setVgap(15);
        registerPane.setPadding(new Insets(20));

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

        Label confirmPasswordLabel = new Label("Подтвердите пароль:");
        confirmPasswordLabel.setFont(new Font("Arial", 18));
        confirmPasswordLabel.setTextFill(Color.WHITE);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPrefWidth(250);
        confirmPasswordField.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10;");

        Button registerButton = new Button("Зарегистрироваться");
        registerButton.setFont(new Font("Arial", 16));
        registerButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        registerButton.setOnMouseEntered(e -> registerButton.setStyle("-fx-background-color: #00A89A; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        Button backButton = new Button("Назад");
        backButton.setFont(new Font("Arial", 16));
        backButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        registerPane.add(loginLabel, 0, 0);
        registerPane.add(loginField, 1, 0);
        registerPane.add(passwordLabel, 0, 1);
        registerPane.add(passwordField, 1, 1);
        registerPane.add(confirmPasswordLabel, 0, 2);
        registerPane.add(confirmPasswordField, 1, 2);
        registerPane.add(registerButton, 1, 3);
        registerPane.add(backButton, 1, 4);

        registerButton.setOnAction(e -> {
            try {
                String login = loginField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (password.equals(confirmPassword) && !login.isEmpty()) {
                    String data = login + ":" + password;
                    Response response = client.sendRequest("REGISTER", data);
                    if (response.getMessage().equals("SUCCESS")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Регистрация успешна!");
                        alert.showAndWait();
                        LoginView loginView = new LoginView(stage, client);
                        loginView.show();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка регистрации!");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Пароли не совпадают или логин пуст!");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, client);
            loginView.show();
        });

        StackPane root = new StackPane(registerPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2D2D2D;");

        Scene registerScene = new Scene(root, 600, 600);
        stage.setScene(registerScene);
        stage.setTitle("Регистрация");
        stage.show();

        loginField.requestFocus();
    }
}