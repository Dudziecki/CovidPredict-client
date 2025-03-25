package com.example.client;

import com.example.client.model.Request;
import com.example.client.model.Response;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response sendRequest(String command, String data) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(new Request(command, data));
            return (Response) in.readObject();
        }
    }
}