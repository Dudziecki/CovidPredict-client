package view;

import com.example.client.Client;
import com.example.client.model.LoginData;
import com.example.client.model.Response;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginView {
    private final Stage stage;
    private final Client client;

    public LoginView(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
    }

    public void show() {
        // Используем StackPane для центрирования
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2D2D2D, #1C2526);");

        // Форма логинизации внутри VBox
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setMaxWidth(300); // Ограничиваем ширину формы

        Label titleLabel = new Label("Вход");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setTextFill(Color.WHITE);

        Label loginLabel = new Label("Логин:");
        loginLabel.setFont(new Font("Arial", 16));
        loginLabel.setTextFill(Color.WHITE);
        TextField loginField = new TextField();
        loginField.setPromptText("Введите логин");
        loginField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");

        loginField.setMaxWidth(250);

        Label passwordLabel = new Label("Пароль:");
        passwordLabel.setFont(new Font("Arial", 16));
        passwordLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Введите пароль");
        passwordField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        passwordField.setMaxWidth(250);

        Button loginButton = new Button("Войти");
        loginButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        loginButton.setPrefWidth(150);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #38E2D5; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;"));

        Button registerButton = new Button("Зарегистрироваться");
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00C4B4; -fx-font-size: 14px; -fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;");
        registerButton.setPrefWidth(150);
        registerButton.setOnMouseEntered(e -> registerButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 14px;-fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00C4B4; -fx-font-size: 14px; -fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;"));

        form.getChildren().addAll(titleLabel, loginLabel, loginField, passwordLabel, passwordField, loginButton, registerButton);
        root.getChildren().add(form);

        loginButton.setOnAction(e -> {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            try {
                LoginData loginData = new LoginData();
                loginData.setUsername(login);
                loginData.setPassword(password);
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(loginData);
                Response response = client.sendRequest("LOGIN", jsonData);
                System.out.println("Login response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String[] parts = response.getMessage().split(":");
                    String role = parts[1];
                    String username = parts.length > 2 ? parts[2] : login;
                    new DashboardView(stage, client, role, username);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Неверный логин или пароль!");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        registerButton.setOnAction(e -> {
            RegisterView registerView = new RegisterView(stage, client);
            registerView.show();
        });

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Вход");

        // Добавляем слушатели на изменение размера окна
        stage.widthProperty().addListener((obs, oldVal, newVal) -> root.requestLayout());
        stage.heightProperty().addListener((obs, oldVal, newVal) -> root.requestLayout());

        stage.show();
    }
}