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
import view.RegisterView;

public class LoginView {
    private final Stage stage;
    private final Client client;

    public LoginView(Stage stage, Client client) {
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
        Label titleLabel = new Label("Вход");
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

        // Кнопка "Войти"
        Button loginButton = new Button("Войти");
        loginButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        loginButton.setPrefWidth(150);

        // Кнопка "Зарегистрироваться"
        Button registerButton = new Button("Зарегистрироваться");
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00C4B4; -fx-font-size: 14px; -fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;");
        registerButton.setPrefWidth(150);

        // Обработчики событий для кнопок
        loginButton.setOnAction(e -> {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();

            // Проверка на пустые поля
            if (login.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            try {
                String data = login + ":" + password;
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
            RegisterView registerView = new RegisterView(stage, client);
            registerView.show();
        });

        // Добавляем элементы в контейнер
        root.getChildren().addAll(titleLabel, loginLabel, loginField, passwordLabel, passwordField, loginButton, registerButton);

        // Создаём сцену
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Вход");
        stage.show();
    }
}