package view;

import com.example.client.Client;
import com.example.client.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        Button logoutButton = new Button("Выйти");


        // Стили для кнопок меню
        historicalDataButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        forecastsButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        compareDataButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        supportButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px;");
        logoutButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

        historicalDataButton.setPrefWidth(200);
        forecastsButton.setPrefWidth(200);
        compareDataButton.setPrefWidth(200);
        supportButton.setPrefWidth(200);
        logoutButton.setPrefWidth(200);

        // Обработчики событий для кнопок
        historicalDataButton.setOnAction(e -> root.setCenter(createHistoricalDataContent()));
        forecastsButton.setOnAction(e -> root.setCenter(createForecastsContent()));
        compareDataButton.setOnAction(e -> root.setCenter(createCompareContent()));
        supportButton.setOnAction(e -> root.setCenter(createSupportContent()));
        logoutButton.setOnAction(e -> new LoginView(stage, client).show());

        sidebar.getChildren().addAll(historicalDataButton, forecastsButton, compareDataButton, supportButton,logoutButton);
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
        regionField.setPromptText("Введите регион (например, Малорита)");
        regionField.setMaxWidth(300);

        Button loadButton = new Button("Загрузить данные");
        loadButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        TableView<EpidemicData> dataTable = new TableView<>();
        dataTable.setPrefHeight(200);

        TableColumn<EpidemicData, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
        dateColumn.setPrefWidth(150);

        TableColumn<EpidemicData, Number> infectedColumn = new TableColumn<>("Число заражённых");
        infectedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<Number>(cellData.getValue().getInfected()));
        infectedColumn.setPrefWidth(150);

        dataTable.getColumns().addAll(dateColumn, infectedColumn);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Дни");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Число заражённых");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Динамика заболеваемости");
        lineChart.setPrefHeight(300);

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
                    List<EpidemicData> epidemicDataList = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(dataJson, new com.fasterxml.jackson.core.type.TypeReference<List<EpidemicData>>(){});

                    dataTable.getItems().clear();
                    lineChart.getData().clear();

                    dataTable.getItems().addAll(epidemicDataList);

                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    series.setName("Число заражённых");
                    for (int i = 0; i < epidemicDataList.size(); i++) {
                        EpidemicData data = epidemicDataList.get(i);
                        series.getData().add(new XYChart.Data<>(i + 1, data.getInfected()));
                    }
                    lineChart.getData().add(series);

                    errorLabel.setText("Данные успешно загружены!");
                    errorLabel.setStyle("-fx aeruginosa: green; -fx-font-size: 14px;");
                } else {
                    errorLabel.setText("Ошибка при загрузке данных: " + response.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(sectionTitle, regionField, loadButton, dataTable, lineChart, errorLabel);
        return content;
    }

    private VBox createForecastsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Прогнозы");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Forecast> forecastTable = new TableView<>();
        forecastTable.setPrefHeight(400);

        TableColumn<Forecast, String> forecastDateColumn = new TableColumn<>("Дата прогноза");
        forecastDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getForecastDate()));
        forecastDateColumn.setPrefWidth(150);

        TableColumn<Forecast, Number> predictedCasesColumn = new TableColumn<>("Прогнозируемое число заражённых");
        predictedCasesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPredictedCases()));
        predictedCasesColumn.setPrefWidth(200);

        TableColumn<Forecast, String> createdAtColumn = new TableColumn<>("Создано");
        createdAtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt()));
        createdAtColumn.setPrefWidth(150);

        forecastTable.getColumns().addAll(forecastDateColumn, predictedCasesColumn, createdAtColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        try {
            Response response = client.sendRequest("GET_FORECASTS", null);
            System.out.println("GET_FORECASTS response: " + response.getMessage());
            if (response.getMessage().startsWith("SUCCESS")) {
                String forecastsJson = response.getMessage().substring("SUCCESS:".length());
                List<Forecast> forecasts = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(forecastsJson, new com.fasterxml.jackson.core.type.TypeReference<List<Forecast>>(){});
                forecastTable.getItems().addAll(forecasts);
            } else {
                errorLabel.setText("Ошибка при загрузке прогнозов: " + response.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Ошибка связи с сервером: " + ex.getMessage());
        }

        content.getChildren().addAll(sectionTitle, forecastTable, errorLabel);
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

        messageTable.getColumns().addAll(messageColumn, statusColumn, responseColumn, createdAtColumn);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

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

    private static class CompareDataResponse {
        private List<EpidemicData> historicalData;
        private List<Forecast> forecasts;

        public List<EpidemicData> getHistoricalData() { return historicalData; }
        public void setHistoricalData(List<EpidemicData> historicalData) { this.historicalData = historicalData; }
        public List<Forecast> getForecasts() { return forecasts; }
        public void setForecasts(List<Forecast> forecasts) { this.forecasts = forecasts; }
    }
}