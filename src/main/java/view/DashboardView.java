package view;

import com.example.client.Client;
import com.example.client.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class DashboardView {
    private final Stage stage;
    private final Client client;
    private final String role;
    private final BorderPane root;

    public DashboardView(Stage stage, Client client, String role) {
        this.stage = stage;
        this.client = client;
        this.role = role;
        this.root = new BorderPane();

        if ("admin".equals(role)) {
            AdminDashboardView adminDashboardView = new AdminDashboardView(stage, client);
            return;
        }

        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();

        Tab inputTab = new Tab("Ввод данных", createInputTabContent());
        inputTab.setClosable(false);

        Tab dataTab = new Tab("Управление данными", createAdminTabContent());
        dataTab.setClosable(false);
        dataTab.setDisable(!"admin".equals(role));

        tabPane.getTabs().addAll(inputTab);

        Button logoutButton = new Button("Выйти");
        logoutButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, client);
            loginView.show();
        });

        HBox topBar = new HBox(10, logoutButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);

        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Панель управления");
        stage.show();
    }
    public void show() {
        if ("admin".equals(role)) {
            return; // Админ уже перенаправлен в конструкторе
        }
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Панель управления");
        stage.show();
    }

    private VBox createInputTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Ввод данных о заражённых");
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
                Response response = client.sendRequest("SUBMIT_DATA", jsonData);
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
        return content;
    }

    private VBox createAdminTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Управление данными");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<EpidemicData> dataTable = new TableView<>();
        dataTable.setPrefHeight(400);

        TableColumn<EpidemicData, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        idColumn.setPrefWidth(50);

        TableColumn<EpidemicData, String> usernameColumn = new TableColumn<>("Пользователь");
        usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        usernameColumn.setPrefWidth(150);

        TableColumn<EpidemicData, String> regionColumn = new TableColumn<>("Регион");
        regionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRegion()));
        regionColumn.setPrefWidth(150);

        TableColumn<EpidemicData, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
        dateColumn.setPrefWidth(100);

        TableColumn<EpidemicData, Integer> infectedColumn = new TableColumn<>("Заражённые");
        infectedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getInfected()).asObject());
        infectedColumn.setPrefWidth(100);

        dataTable.getColumns().addAll(idColumn, usernameColumn, regionColumn, dateColumn, infectedColumn);

        Button refreshButton = new Button("Обновить");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        Button deleteButton = new Button("Удалить запись");
        deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

        refreshButton.setOnAction(e -> {
            try {
                Response response = client.sendRequest("GET_ALL_DATA", "");
                if (response.getMessage().startsWith("SUCCESS")) {
                    String jsonData = response.getMessage().substring("SUCCESS:".length());
                    if (jsonData.isEmpty()) {
                        dataTable.getItems().clear();
                        return;
                    }
                    List<EpidemicData> dataList = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(jsonData, new com.fasterxml.jackson.core.type.TypeReference<List<EpidemicData>>(){});
                    dataTable.getItems().clear();
                    dataTable.getItems().addAll(dataList);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при получении данных: " + response.getMessage());
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        deleteButton.setOnAction(e -> {
            EpidemicData selectedData = dataTable.getSelectionModel().getSelectedItem();
            if (selectedData == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, выберите запись для удаления!");
                alert.showAndWait();
                return;
            }

            try {
                DeleteData deleteData = new DeleteData();
                deleteData.setId(selectedData.getId());
                String jsonData = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(deleteData);
                Response response = client.sendRequest("DELETE_DATA", jsonData);
                if (response.getMessage().equals("SUCCESS")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Запись успешно удалена!");
                    alert.showAndWait();
                    dataTable.getItems().remove(selectedData);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при удалении записи: " + response.getMessage());
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка связи с сервером!");
                alert.showAndWait();
            }
        });

        HBox buttonBox = new HBox(10, refreshButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, dataTable, buttonBox);
        return content;
    }


}