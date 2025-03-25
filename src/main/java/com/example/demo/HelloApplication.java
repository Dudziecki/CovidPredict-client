package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        showWelcomeScreen();
    }

    // 1. Приветственное окно с лоадером
    private void showWelcomeScreen() {
        Label welcomeLabel = new Label("Добро пожаловать!");
        welcomeLabel.setFont(new Font("Arial", 30));
        welcomeLabel.setTextFill(Color.WHITE);

        ProgressIndicator loader = new ProgressIndicator();
        loader.setPrefSize(50, 50);
        loader.setStyle("-fx-progress-color: #00C4B4;");

        VBox welcomeContent = new VBox(20, welcomeLabel, loader);
        welcomeContent.setAlignment(Pos.CENTER);

        StackPane welcomeLayout = new StackPane(welcomeContent);
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setStyle("-fx-background-color: #2D2D2D;");

        Scene welcomeScene = new Scene(welcomeLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome");
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> showLoginScreen());
        delay.play();
    }

    // 2. Форма авторизации
    private void showLoginScreen() {
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
            String login = loginField.getText();
            String password = passwordField.getText();
            if (login.equals("user") && password.equals("pass")) {
                showDashboard();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Неверный логин или пароль!");
                alert.showAndWait();
            }
        });

        registerButton.setOnAction(e -> showRegisterScreen());

        StackPane root = new StackPane(loginPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2D2D2D;");

        Scene loginScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Авторизация");

        // Устанавливаем фокус на поле логина
        loginField.requestFocus();
    }

    // 3. Форма регистрации
    private void showRegisterScreen() {
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
            String login = loginField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (password.equals(confirmPassword) && !login.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Регистрация успешна!");
                alert.showAndWait();
                showLoginScreen();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка: пароли не совпадают или логин пуст!");
                alert.showAndWait();
            }
        });

        backButton.setOnAction(e -> showLoginScreen());

        StackPane root = new StackPane(registerPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2D2D2D;");

        Scene registerScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(registerScene);
        primaryStage.setTitle("Регистрация");

        // Устанавливаем фокус на поле логина
        loginField.requestFocus();
    }

    // 4. Личный кабинет
    private void showDashboard() {
        VBox dashboardPane = new VBox(20);
        dashboardPane.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Добро пожаловать в личный кабинет!");
        welcomeLabel.setFont(new Font("Arial", 20));
        welcomeLabel.setTextFill(Color.WHITE);

        Button logoutButton = new Button("Выйти");
        logoutButton.setFont(new Font("Arial", 16));
        logoutButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle("-fx-background-color: #DD4444; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        dashboardPane.getChildren().addAll(welcomeLabel, logoutButton);

        logoutButton.setOnAction(e -> showLoginScreen());

        StackPane root = new StackPane(dashboardPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2D2D2D;");

        Scene dashboardScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(dashboardScene);
        primaryStage.setTitle("Личный кабинет");
    }

    public static void main(String[] args) {
        launch(args);
    }
}