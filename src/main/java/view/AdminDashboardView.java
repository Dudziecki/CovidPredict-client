package view;

import com.example.client.Client;
import com.example.client.model.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.hansolo.toolbox.Statistics;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminDashboardView {
    private final Stage stage;
    private final Client client;
    private final BorderPane root;
    private final VBox menu;

    public AdminDashboardView(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
        this.root = new BorderPane();
        this.menu = createMenu();
        showStatistics();
        // Устанавливаем меню слева
        root.setLeft(menu);
        // По умолчанию показываем список всех пользователей
        showAllUsers();

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Личный кабинет администратора");
        stage.show();
    }

    private VBox createMenu() {
        VBox menuBox = new VBox(10);
        menuBox.setPadding(new Insets(20));
        menuBox.setStyle("-fx-background-color: #2C3E50; -fx-min-width: 200px;");

        Label titleLabel = new Label("Меню администратора");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

        Button allUsersButton = createMenuButton("Все пользователи");
        allUsersButton.setOnAction(e -> showAllUsers());

        Button findUserButton = createMenuButton("Найти пользователя");
        findUserButton.setOnAction(e -> showFindUser());

        Button addUserButton = createMenuButton("Добавить пользователя");
        addUserButton.setOnAction(e -> showAddUser());

        Button addDataButton = createMenuButton("Добавить данные");
        addDataButton.setOnAction(e -> showAddData());

        Button statsButton = createMenuButton("Статистика");
        statsButton.setOnAction(e -> showStatistics());

        Button forecastButton = createMenuButton("Прогнозирование");
        forecastButton.setOnAction(e -> showForecasting());

        Button logsButton = createMenuButton("Просмотр логов");
        logsButton.setOnAction(e -> showLogs());

        Button supportMessagesButton = createMenuButton("Сообщения поддержки");
//        supportMessagesButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
//        supportMessagesButton.setPrefWidth(200);
        supportMessagesButton.setOnAction(e -> root.setCenter(createSupportMessagesContent()));



        Button logoutButton = createMenuButton("Выйти");
        logoutButton.setOnAction(e -> {
            try {
                client.close();
                LoginView loginView = new LoginView(stage, client);
                loginView.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при выходе: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        menuBox.getChildren().addAll(titleLabel, allUsersButton, findUserButton, addUserButton, addDataButton, logsButton,statsButton,forecastButton, supportMessagesButton,logoutButton);
        return menuBox;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 180px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #1ABC9C; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 180px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 180px;"));
        return button;
    }

    private void showAllUsers() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Список всех пользователей");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<UserData> userTable = new TableView<>();
        userTable.setPrefHeight(400);

        TableColumn<UserData, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
        idColumn.setPrefWidth(50);

        TableColumn<UserData, String> usernameColumn = new TableColumn<>("Логин");
        usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        usernameColumn.setPrefWidth(150);

        TableColumn<UserData, String> roleColumn = new TableColumn<>("Роль");
        roleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        roleColumn.setPrefWidth(100);

        userTable.getColumns().addAll(idColumn, usernameColumn, roleColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        try {
            Response response = client.sendRequest("GET_ALL_USERS", "");
            System.out.println("Initial GET_ALL_USERS response: " + response.getMessage());
            if (response.getMessage().startsWith("SUCCESS")) {
                String jsonData = response.getMessage().substring("SUCCESS:".length());
                if (jsonData.isEmpty()) {
                    userTable.getItems().clear();
                    errorLabel.setText("Нет данных о пользователях.");
                } else {
                    List<UserData> users = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(jsonData, new com.fasterxml.jackson.core.type.TypeReference<List<UserData>>(){});
                    userTable.getItems().clear();
                    userTable.getItems().addAll(users);
                    errorLabel.setText("");
                }
            } else {
                errorLabel.setText("Ошибка при начальной загрузке данных: " + response.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Ошибка связи с сервером при начальной загрузке: " + ex.getMessage());
        }

        Button refreshButton = new Button("Обновить");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> {
            try {
                Response response = client.sendRequest("GET_ALL_USERS", "");
                System.out.println("GET_ALL_USERS response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String jsonData = response.getMessage().substring("SUCCESS:".length());
                    if (jsonData.isEmpty()) {
                        userTable.getItems().clear();
                        errorLabel.setText("Нет данных о пользователей.");
                        return;
                    }
                    List<UserData> users = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(jsonData, new com.fasterxml.jackson.core.type.TypeReference<List<UserData>>(){});
                    userTable.getItems().clear();
                    userTable.getItems().addAll(users);
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Ошибка при получении данных: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        Button deleteButton = new Button("Удалить пользователя");
        deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            UserData selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, выберите пользователя для удаления!");
                alert.showAndWait();
                return;
            }
            try {
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(selectedUser.getUserId());
                Response response = client.sendRequest("DELETE_USER", jsonData);
                System.out.println("DELETE_USER response: " + response.getMessage());
                if (response.getMessage().equals("SUCCESS")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Пользователь успешно удалён!");
                    alert.showAndWait();
                    userTable.getItems().remove(selectedUser);
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Ошибка при удалении: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(10, refreshButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, userTable, buttonBox, errorLabel);
        root.setCenter(content);
    }

    private void showFindUser() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Поиск пользователя");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Введите логин");
        usernameField.setMaxWidth(300);

        Button searchButton = new Button("Найти");
        searchButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 14px;");

        searchButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                resultLabel.setText("Пожалуйста, введите логин!");
                resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                return;
            }
            try {
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(username);
                Response response = client.sendRequest("FIND_USER", jsonData);
                System.out.println("FIND_USER response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String userJson = response.getMessage().substring("SUCCESS:".length());
                    UserData user = new com.fasterxml.jackson.databind.ObjectMapper().readValue(userJson, UserData.class);
                    resultLabel.setText("Найден пользователь: ID=" + user.getUserId() + ", Логин=" + user.getUsername() + ", Роль=" + user.getRole());
                    resultLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                } else {
                    resultLabel.setText("Ошибка: " + response.getMessage());
                    resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                resultLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
                resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            }
        });

        content.getChildren().addAll(title, usernameField, searchButton, resultLabel);
        root.setCenter(content);
    }

    private void showAddUser() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Добавить нового пользователя");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Логин");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        passwordField.setMaxWidth(300);

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("admin", "guest");
        roleComboBox.setPromptText("Выберите роль");
        roleComboBox.setMaxWidth(300);

        Button addButton = new Button("Добавить");
        addButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");

        addButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleComboBox.getSelectionModel().getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            try {
                RegisterData regData = new RegisterData();
                regData.setUsername(username);
                regData.setPassword(password);
                regData.setRole(role);
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(regData);
                Response response = client.sendRequest("ADD_USER", jsonData);
                if (response.getMessage().equals("SUCCESS")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Пользователь успешно добавлен!");
                    alert.showAndWait();
                    usernameField.clear();
                    passwordField.clear();
                    roleComboBox.getSelectionModel().clearSelection();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при добавлении: " + response.getMessage());
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        content.getChildren().addAll(title, usernameField, passwordField, roleComboBox, addButton);
        root.setCenter(content);
    }

    private void showAddData() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Добавить данные о заражённых");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField regionField = new TextField();
        regionField.setPromptText("Регион");
        regionField.setMaxWidth(300);

        TextField dateField = new TextField();
        dateField.setPromptText("Дата (гггг-мм-дд)");
        dateField.setMaxWidth(300);

        TextField infectedField = new TextField();
        infectedField.setPromptText("Количество заражённых");
        infectedField.setMaxWidth(300);

        Button submitButton = new Button("Отправить");
        submitButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        submitButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            String date = dateField.getText().trim();
            String infected = infectedField.getText().trim();

            if (region.isEmpty() || date.isEmpty() || infected.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!");
                alert.showAndWait();
                return;
            }

            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Дата должна быть в формате гггг-мм-дд!");
                alert.showAndWait();
                return;
            }

            try {
                java.time.LocalDate.parse(date);
            } catch (java.time.format.DateTimeParseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Некорректная дата! Убедитесь, что день и месяц правильные.");
                alert.showAndWait();
                return;
            }

            if (!infected.matches("\\d+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Количество заражённых должно быть числом!");
                alert.showAndWait();
                return;
            }

            try {
                EpidemicData epidemicData = new EpidemicData(region, date, Integer.parseInt(infected));
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(epidemicData);
                Response response = client.sendRequest("ADMIN_SUBMIT_DATA", jsonData);
                if (response.getMessage().equals("SUCCESS")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Данные успешно отправлены!");
                    alert.showAndWait();
                    regionField.clear();
                    dateField.clear();
                    infectedField.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при отправке данных: " + response.getMessage());
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        content.getChildren().addAll(title, regionField, dateField, infectedField, submitButton);
        root.setCenter(content);
    }

    private void showLogs() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Просмотр логов");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<LogData> logTable = new TableView<>();
        logTable.setPrefHeight(400);

        TableColumn<LogData, Integer> logIdColumn = new TableColumn<>("ID лога");
        logIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getLogId()).asObject());
        logIdColumn.setPrefWidth(80);

        TableColumn<LogData, Integer> userIdColumn = new TableColumn<>("ID пользователя");
        userIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
        userIdColumn.setPrefWidth(120);

        TableColumn<LogData, String> usernameColumn = new TableColumn<>("Имя пользователя");
        usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        usernameColumn.setPrefWidth(150);

        TableColumn<LogData, String> actionColumn = new TableColumn<>("Действие");
        actionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAction()));
        actionColumn.setPrefWidth(150);

        TableColumn<LogData, String> timestampColumn = new TableColumn<>("Время");
        timestampColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedTimestamp()));
        timestampColumn.setPrefWidth(200);

        logTable.getColumns().addAll(logIdColumn, userIdColumn, usernameColumn, actionColumn, timestampColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Логика загрузки логов
        Runnable loadLogs = () -> {
            try {
                Response response = client.sendRequest("GET_LOGS", "");
                System.out.println("GET_LOGS response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String jsonData = response.getMessage().substring("SUCCESS:".length());
                    if (jsonData.isEmpty()) {
                        logTable.getItems().clear();
                        errorLabel.setText("Нет данных о логах.");
                        return;
                    }
                    List<LogData> logs = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(jsonData, new com.fasterxml.jackson.core.type.TypeReference<List<LogData>>(){});
                    logTable.getItems().clear();
                    logTable.getItems().addAll(logs);
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Ошибка при получении логов: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        };

        // Выполняем начальную загрузку логов
        loadLogs.run();

        Button refreshButton = new Button("Обновить");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadLogs.run());

        content.getChildren().addAll(title, logTable, refreshButton, errorLabel);
        root.setCenter(content);
    }
    private void showStatistics() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Статистика заболеваемости");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Таблица статистики
        TableView<Statistics> statsTable = new TableView<>();
        statsTable.setPrefHeight(200);

        TableColumn<Statistics, String> regionColumn = new TableColumn<>("Регион");
        regionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRegion()));
        regionColumn.setPrefWidth(150);

        TableColumn<Statistics, Double> avgInfectedColumn = new TableColumn<>("Среднее число заражённых");
        avgInfectedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getAvgInfected()).asObject());
        avgInfectedColumn.setPrefWidth(200);

        statsTable.getColumns().addAll(regionColumn, avgInfectedColumn);

        // График заболеваемости
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Дни");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Число заражённых");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Динамика заболеваемости");
        lineChart.setPrefHeight(300);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Загрузка статистики
        Runnable loadStatistics = () -> {
            try {
                Response response = client.sendRequest("GET_STATISTICS", "");
                System.out.println("GET_STATISTICS response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String jsonData = response.getMessage().substring("SUCCESS:".length());
                    if (jsonData.isEmpty() || jsonData.equals("[]")) {
                        statsTable.getItems().clear();
                        errorLabel.setText("Нет данных для отображения.");
                        return;
                    }
                    List<Statistics> statistics = new ObjectMapper()
                            .readValue(jsonData, new com.fasterxml.jackson.core.type.TypeReference<List<Statistics>>(){});
                    statsTable.getItems().clear();
                    statsTable.getItems().addAll(statistics);
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Ошибка при получении статистики: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        };

        // При клике на регион в таблице показываем график
        statsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String selectedRegion = newSelection.getRegion();
                try {
                    Response response = client.sendRequest("GET_EPIDEMIC_DATA", new ObjectMapper().writeValueAsString(selectedRegion));
                    if (response.getMessage().startsWith("SUCCESS")) {
                        String jsonData = response.getMessage().substring("SUCCESS:".length());
                        List<EpidemicData> data = new ObjectMapper()
                                .readValue(jsonData, new com.fasterxml.jackson.core.type.TypeReference<List<EpidemicData>>(){});
                        XYChart.Series<Number, Number> series = new XYChart.Series<>();
                        series.setName(selectedRegion);
                        for (int i = 0; i < data.size(); i++) {
                            series.getData().add(new XYChart.Data<>(i + 1, data.get(i).getInfected()));
                        }
                        lineChart.getData().clear();
                        lineChart.getData().add(series);
                    } else {
                        errorLabel.setText("Ошибка при получении данных: " + response.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
                }
            }
        });

        loadStatistics.run();

        Button refreshButton = new Button("Обновить");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadStatistics.run());

        content.getChildren().addAll(title, statsTable, lineChart, refreshButton, errorLabel);
        root.setCenter(content);
    }
    private void showForecasting() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Прогнозирование");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField regionField = new TextField();
        regionField.setPromptText("Регион");
        regionField.setMaxWidth(300);

        TextField periodField = new TextField();
        periodField.setPromptText("Период (дни)");
        periodField.setMaxWidth(300);

        Button forecastButton = new Button("Создать прогноз");
        forecastButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        TableView<ForecastResult> forecastTable = new TableView<>();
        forecastTable.setPrefHeight(200);

        TableColumn<ForecastResult, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
        dateColumn.setPrefWidth(150);

        TableColumn<ForecastResult, Double> predictedColumn = new TableColumn<>("Прогнозируемое число заражённых");
        predictedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPredictedInfected()).asObject()); // Изменили на SimpleDoubleProperty
        predictedColumn.setPrefWidth(200);

        forecastTable.getColumns().addAll(dateColumn, predictedColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        forecastButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            String periodStr = periodField.getText().trim();

            if (region.isEmpty() || periodStr.isEmpty()) {
                errorLabel.setText("Пожалуйста, заполните все поля!");
                return;
            }

            int period;
            try {
                period = Integer.parseInt(periodStr);
                if (period <= 0) {
                    errorLabel.setText("Период должен быть больше 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Период должен быть числом!");
                return;
            }

            try {
                ForecastRequest forecastRequest = new ForecastRequest(region, period);
                String jsonData = new ObjectMapper().writeValueAsString(forecastRequest);
                System.out.println("Sending FORECAST request with data: " + jsonData);
                Response response = client.sendRequest("FORECAST", jsonData);
                System.out.println("FORECAST response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String forecastJson = response.getMessage().substring("SUCCESS:".length());
                    List<ForecastResult> forecastResults = new ObjectMapper()
                            .readValue(forecastJson, new com.fasterxml.jackson.core.type.TypeReference<List<ForecastResult>>(){});
                    forecastTable.getItems().clear();
                    forecastTable.getItems().addAll(forecastResults);
                    errorLabel.setText("Прогноз успешно создан!");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                } else {
                    errorLabel.setText("Ошибка при создании прогноза: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(title, regionField, periodField, forecastButton, forecastTable, errorLabel);
        root.setCenter(content);
    }
    private void loadMessages(TableView<SupportMessage> messageTable, Label errorLabel) {
        try {
            Response response = client.sendRequest("GET_ALL_MESSAGES", null);
            System.out.println("GET_ALL_MESSAGES response: " + response.getMessage());
            if (response.getMessage().startsWith("SUCCESS")) {
                String messagesJson = response.getMessage().substring("SUCCESS:".length());
                List<SupportMessage> messages = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(messagesJson, new com.fasterxml.jackson.core.type.TypeReference<List<SupportMessage>>(){});
                messageTable.getItems().clear();
                messageTable.getItems().addAll(messages);
            } else {
                errorLabel.setText("Ошибка при загрузке сообщений: " + response.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
        }
    }
    private VBox createSupportMessagesContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Сообщения поддержки");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<SupportMessage> messageTable = new TableView<>();
        messageTable.setPrefHeight(300);

        TableColumn<SupportMessage, String> usernameColumn = new TableColumn<>("Пользователь");
        usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        usernameColumn.setPrefWidth(100);

        TableColumn<SupportMessage, String> messageColumn = new TableColumn<>("Сообщение");
        messageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMessage()));
        messageColumn.setPrefWidth(200);

        TableColumn<SupportMessage, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusColumn.setPrefWidth(100);

        TableColumn<SupportMessage, String> responseColumn = new TableColumn<>("Ответ");
        responseColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getResponse() != null ? cellData.getValue().getResponse() : "Ожидает ответа"));
        responseColumn.setPrefWidth(200);

        TableColumn<SupportMessage, String> createdAtColumn = new TableColumn<>("Отправлено");
        createdAtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt()));
        createdAtColumn.setPrefWidth(150);

        messageTable.getColumns().addAll(usernameColumn, messageColumn, statusColumn, responseColumn, createdAtColumn);

        TextArea responseField = new TextArea();
        responseField.setPromptText("Введите ответ на сообщение...");
        responseField.setPrefRowCount(3);
        responseField.setMaxWidth(400);

        Button respondButton = new Button("Отправить ответ");
        respondButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Загружаем сообщения при открытии вкладки
        loadMessages(messageTable, errorLabel);

        // Обработчик для выбора сообщения
        messageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                responseField.setText(newSelection.getResponse() != null ? newSelection.getResponse() : "");
            }
        });

        // Обработчик для отправки ответа
        respondButton.setOnAction(e -> {
            SupportMessage selectedMessage = messageTable.getSelectionModel().getSelectedItem();
            if (selectedMessage == null) {
                errorLabel.setText("Пожалуйста, выберите сообщение для ответа!");
                return;
            }

            String responseText = responseField.getText().trim();
            if (responseText.isEmpty()) {
                errorLabel.setText("Пожалуйста, введите текст ответа!");
                return;
            }

            // Обновляем статус и ответ в выбранном сообщении
            selectedMessage.setStatus("RESPONDED");
            selectedMessage.setResponse(responseText);

            try {
                String requestData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(selectedMessage);
                Response response = client.sendRequest("RESPOND_MESSAGE", requestData);
                System.out.println("RESPOND_MESSAGE response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    errorLabel.setText("Ответ успешно отправлен!");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                    responseField.clear();
                    loadMessages(messageTable, errorLabel); // Обновляем таблицу
                } else {
                    errorLabel.setText("Ошибка при отправке ответа: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(sectionTitle, messageTable, responseField, respondButton, errorLabel);
        return content;
    }
    public static class Statistics {
        private String region;
        private double avgInfected;

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public double getAvgInfected() { return avgInfected; }
        public void setAvgInfected(double avgInfected) { this.avgInfected = avgInfected; }
    }

    public static class UserData {
        private final int userId;
        private final String username;
        private final String password;
        private final String role;

        @JsonCreator
        public UserData(
                @JsonProperty("userId") int userId,
                @JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("role") String role) {
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }
    }

    public static class LogData {
        private int logId;
        private int userId;
        private String username;
        private String action;
        private long timestamp;

        public LogData() {
        }

        @JsonCreator
        public LogData(
                @JsonProperty("id") int logId,
                @JsonProperty("userId") int userId,
                @JsonProperty("username") String username,
                @JsonProperty("action") String action,
                @JsonProperty("timestamp") long timestamp) {
            this.logId = logId;
            this.userId = userId;
            this.username = username;
            this.action = action;
            this.timestamp = timestamp;
        }

        public int getLogId() { return logId; }
        public void setLogId(int logId) { this.logId = logId; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public String getFormattedTimestamp() {
            java.time.Instant instant = java.time.Instant.ofEpochMilli(timestamp);
            return instant.atZone(java.time.ZoneId.systemDefault()).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public static class ForecastRequest {
        private String region;
        private int period;

        public ForecastRequest(String region, int period) {
            this.region = region;
            this.period = period;
        }

        public String getRegion() {
            return region;
        }

        public int getPeriod() {
            return period;
        }
    }

    public static class ForecastResult {
        private String date;
        private double predictedInfected; // Изменили на double

        @JsonCreator
        public ForecastResult(
                @JsonProperty("date") String date,
                @JsonProperty("predictedInfected") double predictedInfected) {
            this.date = date;
            this.predictedInfected = predictedInfected;
        }

        public String getDate() {
            return date;
        }

        public double getPredictedInfected() {
            return predictedInfected;
        }
    }
}