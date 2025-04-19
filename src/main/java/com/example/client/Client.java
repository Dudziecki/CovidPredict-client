package com.example.client;

import com.example.client.model.Request;
import com.example.client.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class Client {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final ObjectMapper objectMapper;

    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.objectMapper = new ObjectMapper();
    }

    public Response sendRequest(String command, String data) throws IOException {
        Request request = new Request(command, data);
        // Сериализуем объект в JSON
        String jsonRequest = objectMapper.writeValueAsString(request);
        // Отправляем JSON-строку
        writer.write(jsonRequest + "\n");
        writer.flush();

        // Читаем ответ
        String jsonResponse = reader.readLine();
        // Десериализуем JSON в объект Response
        return objectMapper.readValue(jsonResponse, Response.class);
    }

    public void close() throws IOException {
        socket.close();
    }
}