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
import view.LoginView;

public class RegisterView {
    private final Stage stage;
    private final Client client;

    public RegisterView(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
    }

    public void show() {
        // Основной контейнер с градиентом
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2D2D2D, #1C2526);");

        // Заголовок
        Label titleLabel = new Label("Регистрация");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setTextFill(Color.WHITE);

        // Поле логина
        Label loginLabel = new Label("Логин:");
        loginLabel.setFont(new Font("Arial", 16));
        loginLabel.setTextFill(Color.WHITE);
        TextField loginField = new TextField();
        loginField.setPromptText("Введите логин");
        loginField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        loginField.setMaxWidth(250);

        // Поле пароля
        Label passwordLabel = new Label("Пароль:");
        passwordLabel.setFont(new Font("Arial", 16));
        passwordLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Введите пароль");
        passwordField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        passwordField.setMaxWidth(250);

        // Поле подтверждения пароля
        Label confirmPasswordLabel = new Label("Подтвердите пароль:");
        confirmPasswordLabel.setFont(new Font("Arial", 16));
        confirmPasswordLabel.setTextFill(Color.WHITE);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Повторите пароль");
        confirmPasswordField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        confirmPasswordField.setMaxWidth(250);

        // Кнопка "Зарегистрироваться"
        Button registerButton = new Button("Зарегистрироваться");
        registerButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        registerButton.setPrefWidth(150);

        // Кнопка "Назад"
        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00C4B4; -fx-font-size: 14px; -fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;");
        backButton.setPrefWidth(150);

        // Обработчики событий для кнопок
        registerButton.setOnAction(e -> {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            // Проверка на пустые поля
            if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            // Проверка совпадения паролей
            if (!password.equals(confirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Пароли не совпадают!");
                alert.showAndWait();
                return;
            }

            try {
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

        // Добавляем элементы в контейнер
        root.getChildren().addAll(titleLabel, loginLabel, loginField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, registerButton, backButton);

        // Создаём сцену
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Регистрация");
        stage.show();
    }
}