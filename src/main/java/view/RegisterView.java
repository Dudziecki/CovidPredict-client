package view;

import com.example.client.Client;
import com.example.client.model.RegisterData;
import com.example.client.model.Response;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import view.LoginView;

public class RegisterView {
    private final Stage stage;
    private final Client client;
    private final VBox root;

    public RegisterView(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
        this.root = new VBox(15);
        initialize();
    }

    private void initialize() {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2D2D2D, #1C2526);");

        Label titleLabel = new Label("Регистрация");
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

        Button registerButton = new Button("Зарегистрироваться");
        registerButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        registerButton.setPrefWidth(150);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00C4B4; -fx-font-size: 14px; -fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;");
        backButton.setPrefWidth(150);

        registerButton.setOnAction(e -> {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            try {
                RegisterData regData = new RegisterData();
                regData.setUsername(login);
                regData.setPassword(password);
                regData.setRole("guest");
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(regData);
                Response response = client.sendRequest("REGISTER", jsonData);
                if (response.getMessage().equals("SUCCESS")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Регистрация успешна!");
                    alert.showAndWait();
                    LoginView loginView = new LoginView(stage, client);
                    loginView.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Пользователь уже существует!");
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

        root.getChildren().addAll(titleLabel, loginLabel, loginField, passwordLabel, passwordField, registerButton, backButton);
    }

    public void show() {
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Регистрация");
        stage.show();
    }
}