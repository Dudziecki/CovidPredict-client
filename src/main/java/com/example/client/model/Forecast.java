package com.example.client.model;

public class Forecast {
    private String forecastDate;
    private int predictedCases;
    private String createdAt;

    // Конструктор по умолчанию (нужен для десериализации Jackson)
    public Forecast() {}

    // Геттеры и сеттеры
    public String getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }

    public int getPredictedCases() {
        return predictedCases;
    }

    public void setPredictedCases(int predictedCases) {
        this.predictedCases = predictedCases;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}