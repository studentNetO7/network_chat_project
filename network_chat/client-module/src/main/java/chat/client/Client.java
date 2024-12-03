package chat.client;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import chat.server.PortReader;

public class Client {
    private static final String LOG_FILE = "file.log";
    private static String name;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter your name:");
        name = reader.readLine();

        // Получение порта через SettingsReader
        int port = PortReader.readPort();
        // Подключение к серверу
        Socket socket = new Socket("localhost", port);
        PrintWriter outClient = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Логирование сообщений
        logMessage("Connected to server");
        // Отправляем имя клиентом на сервер
        outClient.println(name);

        // Чтение сообщений с клавиатуры
        String message;
        while (true) {
            System.out.print(name + ": ");
            message = reader.readLine();
            if (message.equalsIgnoreCase("/exit")) {
                break;
            }

            outClient.println(message);
            // logMessage(name + ": " + message);

            String serverMessage = inClient.readLine();
            System.out.println(serverMessage);
        }

        socket.close();
    }
    // Метод для логирования сообщений
    private static void logMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

