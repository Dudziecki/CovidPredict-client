package com.example.client;

import view.LoginView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.IOException;

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

        // Переход к форме авторизации через 3 секунды
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                // Создаем клиент для взаимодействия с сервером
                Client client = new Client("localhost", 8080);
                // Переходим к экрану авторизации
                LoginView loginView = new LoginView(primaryStage, client);
                loginView.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Показываем сообщение об ошибке пользователю
                Label errorLabel = new Label("Не удалось подключиться к серверу!");
                errorLabel.setFont(new Font("Arial", 20));
                errorLabel.setTextFill(Color.RED);

                StackPane errorLayout = new StackPane(errorLabel);
                errorLayout.setAlignment(Pos.CENTER);
                errorLayout.setStyle("-fx-background-color: #2D2D2D;");

                Scene errorScene = new Scene(errorLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
                primaryStage.setScene(errorScene);
            }
        });
        delay.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}