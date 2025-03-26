package com.example.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    @JsonProperty("message")
    private String message;

    public Response() {} // Пустой конструктор для Jackson

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}