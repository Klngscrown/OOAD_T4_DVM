package org.DVM.Control.Communication;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class JsonSocketService implements SocketService {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private final Gson gson = new Gson();

    public JsonSocketService(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void start() {
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Error initializing streams", e);
        }
    }

    @Override
    public void stop() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException("Error closing streams", e);
        }
    }

    @Override
    public void handleClient(Socket clientSocket, MessageCallback callback) {
        try {
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                System.out.println("Received at JsonSoketService: " + inputLine);

                // JSON 문자열을 Message 객체로 변환하는 로직 필요
                Message receivedMessage = gson.fromJson(inputLine, Message.class);

                // 콜백으로 메시지 전달
                callback.onMessageReceived(receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Object message) {
        System.out.println("JSON : " + gson.toJson(message));

        writer.println(gson.toJson(message));
    }

    @Override
    public <T> T receiveMessage(Class<T> clazz) {
        try {
            return gson.fromJson(reader.readLine(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error receiving message", e);
        }
    }
}
