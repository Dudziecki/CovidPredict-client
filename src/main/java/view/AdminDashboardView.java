package view;

import com.example.client.Client;
import com.example.client.model.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

        Button logoutButton = createMenuButton("Выйти");
        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, client);
            loginView.show();
        });

        menuBox.getChildren().addAll(titleLabel, allUsersButton, findUserButton, addUserButton, addDataButton, logoutButton);
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

        // Начальная загрузка данных
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
                        errorLabel.setText("Нет данных о пользователях.");
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

    // Внутренний класс для представления пользователей в таблице


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
}