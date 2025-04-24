package com.example.client;

import com.example.client.model.Request;
import com.example.client.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final ObjectMapper objectMapper;
    private final String host; // Сохраняем host
    private final int port;    // Сохраняем port

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.objectMapper = new ObjectMapper();
        connect(); // Устанавливаем соединение при создании объекта
    }

    // Метод для установки или восстановления соединения
    private void connect() throws IOException {
        // Закрываем старое соединение, если оно существует
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public Response sendRequest(String command, String data) throws IOException {
        // Если сокет закрыт, восстанавливаем соединение
        if (socket == null || socket.isClosed()) {
            connect();
        }

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
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
}