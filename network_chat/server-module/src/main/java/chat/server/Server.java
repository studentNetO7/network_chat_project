package chat.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    private static final String LOG_FILE = "file.log";

    public static void main(String[] args) throws IOException {
        // класс будет слушать соединения от клиентов, обрабатывать их сообщения и записывать в лог.

        // Получение порта через PortReader
        int PORT = PortReader.readPort();

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Обрабатываем клиента в отдельном потоке
            new ClientHandler(clientSocket).start();
        }
    }

    public static void logMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



