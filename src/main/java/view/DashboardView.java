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

public class DashboardView {
    private final Stage stage;
    private final Client client;
    private final String userRole;

    public DashboardView(Stage stage, Client client, String userRole) {
        this.stage = stage;
        this.client = client;
        this.userRole = userRole;
    }

    public void show() {
        // Основной контейнер с градиентом
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2D2D2D, #1C2526);");

        // Заголовок
        Label titleLabel = new Label("Панель управления");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setTextFill(Color.WHITE);

        // Информация о роли пользователя
        Label roleLabel = new Label("Роль: " + userRole);
        roleLabel.setFont(new Font("Arial", 16));
        roleLabel.setTextFill(Color.LIGHTGRAY);

        // Форма для ввода данных
        Label regionLabel = new Label("Регион:");
        regionLabel.setFont(new Font("Arial", 16));
        regionLabel.setTextFill(Color.WHITE);
        TextField regionField = new TextField();
        regionField.setPromptText("Введите регион");
        regionField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        regionField.setMaxWidth(250);

        Label dateLabel = new Label("Дата (гггг-мм-дд):");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        TextField dateField = new TextField();
        dateField.setPromptText("Например, 2025-04-20");
        dateField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        dateField.setMaxWidth(250);

        Label infectedLabel = new Label("Количество заражённых:");
        infectedLabel.setFont(new Font("Arial", 16));
        infectedLabel.setTextFill(Color.WHITE);
        TextField infectedField = new TextField();
        infectedField.setPromptText("Введите число");
        infectedField.setStyle("-fx-background-color: #3C3F41; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-font-size: 14px;");
        infectedField.setMaxWidth(250);

        // Кнопка "Отправить данные"
        Button submitButton = new Button("Отправить данные");
        submitButton.setStyle("-fx-background-color: #00C4B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        submitButton.setPrefWidth(150);

        // Кнопка "Выйти"
        Button logoutButton = new Button("Выйти");
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00C4B4; -fx-font-size: 14px; -fx-border-color: #00C4B4; -fx-border-width: 1px; -fx-padding: 5 10;");
        logoutButton.setPrefWidth(150);

        // Обработчик для кнопки "Отправить данные"
        submitButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            String date = dateField.getText().trim();
            String infected = infectedField.getText().trim();

            // Проверка на пустые поля
            if (region.isEmpty() || date.isEmpty() || infected.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            // Проверка формата даты (простая проверка)
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Дата должна быть в формате гггг-мм-дд!");
                alert.showAndWait();
                return;
            }

            // Проверка, что количество заражённых — это число
            if (!infected.matches("\\d+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Количество заражённых должно быть числом!");
                alert.showAndWait();
                return;
            }

            try {
                // Формируем данные для отправки: region:date:infected
                String data = region + ":" + date + ":" + infected;
                Response response = client.sendRequest("SUBMIT_DATA", data);
                if (response.getMessage().equals("SUCCESS")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Данные успешно отправлены!");
                    alert.showAndWait();
                    // Очищаем поля после успешной отправки
                    regionField.clear();
                    dateField.clear();
                    infectedField.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при отправке данных!");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        // Обработчик для кнопки "Выйти"
        logoutButton.setOnAction(e -> {
            try {
                client.close();
                LoginView loginView = new LoginView(stage, new Client("localhost", 8080));
                loginView.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при выходе!");
                alert.showAndWait();
            }
        });

        // Добавляем элементы в контейнер
        root.getChildren().addAll(titleLabel, roleLabel, regionLabel, regionField, dateLabel, dateField, infectedLabel, infectedField, submitButton, logoutButton);

        // Создаём сцену
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Панель управления");
        stage.show();
    }
}