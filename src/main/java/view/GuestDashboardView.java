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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

public class GuestDashboardView {
    private final Stage stage;
    private final Client client;
    private final String username;
    private final BorderPane root;

    public GuestDashboardView(Stage stage, Client client, String username) {
        this.stage = stage;
        this.client = client;
        this.username = username;
        this.root = new BorderPane();
        initialize();
    }

    private void initialize() {
        // Верхняя панель с заголовком и именем пользователя
        Label title = new Label("Гостевой интерфейс");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label userLabel = new Label("👤 " + username);
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3498DB;");

        HBox topBar = new HBox(20, title, userLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        // Боковое меню
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #D3D3D3; -fx-border-width: 0 1 0 0;");

        Button historicalDataButton = new Button("Исторические данные");
        Button forecastsButton = new Button("Прогнозы");
        Button compareDataButton = new Button("Сравнение данных");
        Button supportButton = new Button("Поддержка");
        Button rankingButton = new Button("Рейтинг регионов");

        Button logoutButton = new Button("Выйти");

//        Tab rankingTab = new Tab("Рейтинг регионов", createRegionRankingContent());
//        rankingTab.setClosable(false);
//        tabPane.getTabs().add(rankingTab);


        // Стили для кнопок меню
        historicalDataButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        forecastsButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        compareDataButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        supportButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        rankingButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        logoutButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

        historicalDataButton.setPrefWidth(200);
        forecastsButton.setPrefWidth(200);
        compareDataButton.setPrefWidth(200);
        supportButton.setPrefWidth(200);
        rankingButton.setPrefWidth(200);
        logoutButton.setPrefWidth(200);

        // Обработчики событий для кнопок
        historicalDataButton.setOnAction(e -> root.setCenter(createHistoricalDataContent()));
        forecastsButton.setOnAction(e -> root.setCenter(createForecastContent()));
        compareDataButton.setOnAction(e -> root.setCenter(createCompareContent()));
        supportButton.setOnAction(e -> root.setCenter(createSupportContent()));

        rankingButton.setOnAction(e -> root.setCenter(createRegionRankingContent()));
        logoutButton.setOnAction(e -> new LoginView(stage, client).show());

        sidebar.getChildren().addAll(historicalDataButton, forecastsButton, compareDataButton,supportButton, rankingButton,logoutButton);
        root.setLeft(sidebar);

        // По умолчанию показываем "Исторические данные"
        root.setCenter(createHistoricalDataContent());

        // Кнопка выхода



//        VBox bottomBox = new VBox(10, logoutButton);
//        bottomBox.setAlignment(Pos.CENTER);
//        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Гостевой интерфейс");
        stage.show();
    }

    private VBox createHistoricalDataContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Исторические данные");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField regionField = new TextField();
        regionField.setPromptText("Введите регион...");
        regionField.setMaxWidth(200);

        Button loadButton = new Button("Загрузить данные");
        loadButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        Button exportButton = new Button("Экспорт в CSV");
        exportButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
        exportButton.setDisable(true); // Активна только после загрузки данных

        TableView<EpidemicData> dataTable = new TableView<>();
        dataTable.setPrefHeight(300);

        TableColumn<EpidemicData, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
        dateColumn.setPrefWidth(150);

        TableColumn<EpidemicData, String> regionColumn = new TableColumn<>("Регион");
        regionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRegion()));
        regionColumn.setPrefWidth(150);

        TableColumn<EpidemicData, Number> infectedColumn = new TableColumn<>("Заражённых");
        infectedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getInfected()));
        infectedColumn.setPrefWidth(150);

        dataTable.getColumns().addAll(dateColumn, regionColumn, infectedColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        loadButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            if (region.isEmpty()) {
                errorLabel.setText("Пожалуйста, введите регион!");
                return;
            }

            try {
                Response response = client.sendRequest("GET_EPIDEMIC_DATA", "\"" + region + "\"");
                System.out.println("GET_EPIDEMIC_DATA response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String dataJson = response.getMessage().substring("SUCCESS:".length());
                    List<EpidemicData> data = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(dataJson, new com.fasterxml.jackson.core.type.TypeReference<List<EpidemicData>>(){});
                    dataTable.getItems().clear();
                    dataTable.getItems().addAll(data);
                    errorLabel.setText("Данные загружены!");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                    exportButton.setDisable(false); // Активируем кнопку экспорта
                } else {
                    errorLabel.setText("Ошибка при загрузке данных: " + response.getMessage());
                    exportButton.setDisable(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
                exportButton.setDisable(true);
            }
        });

        exportButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            try {
                String requestData = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(new ExportRequest("historical", region));
                Response response = client.sendRequest("EXPORT_DATA", requestData);
                System.out.println("EXPORT_DATA response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String csvData = response.getMessage().substring("SUCCESS:".length());
                    File file = new File("historical_data_" + region + ".csv");
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(csvData);
                    }
                    errorLabel.setText("Данные экспортированы в " + file.getAbsolutePath());
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                } else {
                    errorLabel.setText("Ошибка при экспорте данных: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка при экспорте: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(sectionTitle, regionField, loadButton, exportButton, dataTable, errorLabel);
        return content;
    }

    private VBox createForecastContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Прогнозы");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField regionField = new TextField();
        regionField.setPromptText("Введите регион для фильтра...");
        regionField.setMaxWidth(200);

        Button loadButton = new Button("Загрузить прогнозы");
        loadButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        Button exportButton = new Button("Экспорт в CSV");
        exportButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
        exportButton.setDisable(true);

        TableView<Forecast> forecastTable = new TableView<>();
        forecastTable.setPrefHeight(300);

        TableColumn<Forecast, String> dateColumn = new TableColumn<>("Дата прогноза");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getForecastDate()));
        dateColumn.setPrefWidth(150);

        TableColumn<Forecast, Number> predictedCasesColumn = new TableColumn<>("Предсказанные случаи");
        predictedCasesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPredictedCases()));
        predictedCasesColumn.setPrefWidth(150);

        TableColumn<Forecast, String> createdAtColumn = new TableColumn<>("Создано");
        createdAtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt()));
        createdAtColumn.setPrefWidth(150);

        forecastTable.getColumns().addAll(dateColumn, predictedCasesColumn, createdAtColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        loadButton.setOnAction(e -> {
            try {
                Response response = client.sendRequest("GET_FORECASTS", null);
                System.out.println("GET_FORECASTS response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String forecastsJson = response.getMessage().substring("SUCCESS:".length());
                    System.out.println("Received forecast JSON: " + forecastsJson); // Отладка
                    List<Forecast> forecasts = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(forecastsJson, new com.fasterxml.jackson.core.type.TypeReference<List<Forecast>>(){});
                    System.out.println("Deserialized forecasts: " + forecasts); // Отладка
                    String regionFilter = regionField.getText().trim().toLowerCase();
                    System.out.println("Region filter: " + regionFilter); // Отладка
                    forecastTable.getItems().clear();
                    if (regionFilter.isEmpty()) {
                        if (forecasts.isEmpty()) {
                            errorLabel.setText("Нет прогнозов для отображения!");
                            errorLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 14px;");
                        } else {
                            forecastTable.getItems().addAll(forecasts);
                            errorLabel.setText("Прогнозы загружены!");
                            errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                        }
                    } else {
                        List<Forecast> filteredForecasts = forecasts.stream()
                                .filter(f -> f.getRegionName() != null && f.getRegionName().toLowerCase().contains(regionFilter))
                                .collect(Collectors.toList());
                        System.out.println("Filtered forecasts: " + filteredForecasts); // Отладка
                        if (filteredForecasts.isEmpty()) {
                            errorLabel.setText("Нет прогнозов для региона: " + regionFilter);
                            errorLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 14px;");
                        } else {
                            forecastTable.getItems().addAll(filteredForecasts);
                            errorLabel.setText("Прогнозы загружены!");
                            errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                        }
                    }
                    exportButton.setDisable(false);
                } else {
                    errorLabel.setText("Ошибка при загрузке прогнозов: " + response.getMessage());
                    exportButton.setDisable(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
                exportButton.setDisable(true);
            }
        });

        exportButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            try {
                String requestData = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(new ExportRequest("forecast", region.isEmpty() ? "all" : region));
                Response response = client.sendRequest("EXPORT_DATA", requestData);
                System.out.println("EXPORT_DATA response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String csvData = response.getMessage().substring("SUCCESS:".length());
                    File file = new File("forecast_data_" + (region.isEmpty() ? "all" : region) + ".csv");
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(csvData);
                    }
                    errorLabel.setText("Прогнозы экспортированы в " + file.getAbsolutePath());
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                } else {
                    errorLabel.setText("Ошибка при экспорте прогнозов: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка при экспорте: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(sectionTitle, regionField, loadButton, exportButton, forecastTable, errorLabel);
        return content;
    }

    private VBox createCompareContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Сравнение данных");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField regionField = new TextField();
        regionField.setPromptText("Введите регион (например, Малорита)");
        regionField.setMaxWidth(300);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Начальная дата");
        startDatePicker.setMaxWidth(300);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Конечная дата");
        endDatePicker.setMaxWidth(300);

        Button compareButton = new Button("Сравнить");
        compareButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Дни");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Число заражённых");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Сравнение прогнозируемых и реальных данных");
        lineChart.setPrefHeight(400);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        compareButton.setOnAction(e -> {
            String region = regionField.getText().trim();
            if (region.isEmpty()) {
                errorLabel.setText("Пожалуйста, введите регион!");
                return;
            }
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                errorLabel.setText("Пожалуйста, выберите даты!");
                return;
            }

            String startDate = startDatePicker.getValue().toString();
            String endDate = endDatePicker.getValue().toString();

            try {
                String requestData = "{\"region\":\"" + region + "\",\"startDate\":\"" + startDate + "\",\"endDate\":\"" + endDate + "\"}";
                Response response = client.sendRequest("COMPARE_DATA", requestData);
                System.out.println("COMPARE_DATA response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    String dataJson = response.getMessage().substring("SUCCESS:".length());
                    CompareDataResponse compareData = new com.fasterxml.jackson.databind.ObjectMapper().readValue(dataJson, CompareDataResponse.class);

                    lineChart.getData().clear();

                    XYChart.Series<Number, Number> historicalSeries = new XYChart.Series<>();
                    historicalSeries.setName("Реальные данные");
                    List<EpidemicData> historicalData = compareData.getHistoricalData();
                    for (int i = 0; i < historicalData.size(); i++) {
                        EpidemicData data = historicalData.get(i);
                        historicalSeries.getData().add(new XYChart.Data<>(i + 1, data.getInfected()));
                    }
                    lineChart.getData().add(historicalSeries);

                    XYChart.Series<Number, Number> forecastSeries = new XYChart.Series<>();
                    forecastSeries.setName("Прогнозируемые данные");
                    List<Forecast> forecasts = compareData.getForecasts();
                    for (int i = 0; i < forecasts.size(); i++) {
                        Forecast forecast = forecasts.get(i);
                        forecastSeries.getData().add(new XYChart.Data<>(i + 1, forecast.getPredictedCases()));
                    }
                    lineChart.getData().add(forecastSeries);

                    errorLabel.setText("Данные успешно загружены!");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                } else {
                    errorLabel.setText("Ошибка при загрузке данных: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(sectionTitle, regionField, startDatePicker, endDatePicker, compareButton, lineChart, errorLabel);
        return content;
    }

    private VBox createSupportContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Поддержка");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea messageField = new TextArea();
        messageField.setPromptText("Введите ваше сообщение...");
        messageField.setPrefRowCount(5);
        messageField.setMaxWidth(400);

        Button sendButton = new Button("Отправить сообщение");
        sendButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

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

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Проверяем количество непрочитанных сообщений
        try {
            Response response = client.sendRequest("GET_UNREAD_MESSAGES_COUNT", null);
            System.out.println("GET_UNREAD_MESSAGES_COUNT response: " + response.getMessage());
            if (response.getMessage().startsWith("SUCCESS")) {
                int unreadCount = Integer.parseInt(response.getMessage().substring("SUCCESS:".length()));
                if (unreadCount > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Новые ответы");
                    alert.setHeaderText(null);
                    alert.setContentText("У вас есть " + unreadCount + " новых ответов в поддержке!");
                    alert.showAndWait();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
        }

        loadMessages(messageTable, errorLabel);

        sendButton.setOnAction(e -> {
            String message = messageField.getText().trim();
            if (message.isEmpty()) {
                errorLabel.setText("Пожалуйста, введите сообщение!");
                return;
            }

            try {
                Response response = client.sendRequest("SEND_MESSAGE", "\"" + message + "\"");
                System.out.println("SEND_MESSAGE response: " + response.getMessage());
                if (response.getMessage().startsWith("SUCCESS")) {
                    errorLabel.setText("Сообщение отправлено!");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                    messageField.clear();
                    loadMessages(messageTable, errorLabel);
                } else {
                    errorLabel.setText("Ошибка при отправке сообщения: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(sectionTitle, messageField, sendButton, messageTable, errorLabel);
        return content;
    }

    private void loadMessages(TableView<SupportMessage> messageTable, Label errorLabel) {
        try {
            Response response = client.sendRequest("GET_USER_MESSAGES", null);
            System.out.println("GET_USER_MESSAGES response: " + response.getMessage());
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
    private VBox createRegionRankingContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Рейтинг регионов");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<RegionRanking> rankingTable = new TableView<>();
        rankingTable.setPrefHeight(300);

        TableColumn<RegionRanking, String> regionColumn = new TableColumn<>("Регион");
        regionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRegion()));
        regionColumn.setPrefWidth(200);

        TableColumn<RegionRanking, Number> totalInfectedColumn = new TableColumn<>("Всего заражённых");
        totalInfectedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTotalInfected()));
        totalInfectedColumn.setPrefWidth(150);

        rankingTable.getColumns().addAll(regionColumn, totalInfectedColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Загружаем рейтинг
        try {
            Response response = client.sendRequest("GET_REGION_RANKING", null);
            System.out.println("GET_REGION_RANKING response: " + response.getMessage());
            if (response.getMessage().startsWith("SUCCESS")) {
                String rankingsJson = response.getMessage().substring("SUCCESS:".length());
                List<RegionRanking> rankings = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(rankingsJson, new com.fasterxml.jackson.core.type.TypeReference<List<RegionRanking>>(){});
                rankingTable.getItems().clear();
                rankingTable.getItems().addAll(rankings);
            } else {
                errorLabel.setText("Ошибка при загрузке рейтинга: " + response.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
        }

        content.getChildren().addAll(sectionTitle, rankingTable, errorLabel);
        return content;
    }
    private static class Forecast {
        private String forecastDate;
        private int predictedCases;
        private String createdAt;
        private String regionName; // Добавляем поле regionName

        @JsonCreator
        public Forecast(
                @JsonProperty("forecastDate") String forecastDate,
                @JsonProperty("predictedCases") int predictedCases,
                @JsonProperty("createdAt") String createdAt,
                @JsonProperty("regionName") String regionName) { // Добавляем regionName в конструктор
            this.forecastDate = forecastDate;
            this.predictedCases = predictedCases;
            this.createdAt = createdAt;
            this.regionName = regionName;
        }

        public String getForecastDate() { return forecastDate; }
        public int getPredictedCases() { return predictedCases; }
        public String getCreatedAt() { return createdAt; }
        public String getRegionName() { return regionName; } // Добавляем геттер для regionName
    }

    private static class ExportRequest {
        private String dataType;
        private String region;

        public ExportRequest(String dataType, String region) {
            this.dataType = dataType;
            this.region = region;
        }

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
    }

    private static class CompareDataResponse {
        private List<EpidemicData> historicalData;
        private List<Forecast> forecasts;

        public List<EpidemicData> getHistoricalData() { return historicalData; }
        public void setHistoricalData(List<EpidemicData> historicalData) { this.historicalData = historicalData; }
        public List<Forecast> getForecasts() { return forecasts; }
        public void setForecasts(List<Forecast> forecasts) { this.forecasts = forecasts; }
    }
}