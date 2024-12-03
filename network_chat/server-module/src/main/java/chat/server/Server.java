package chat.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final String LOG_FILE = "file.log";
    private static AtomicBoolean isRunning = new AtomicBoolean(true);
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        int PORT = PortReader.readPort();
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        // Используем коллекцию для отслеживания активных клиентских потоков
        List<Thread> clientThreads = new ArrayList<>();

        // Ожидаем подключения клиентов
        while (isRunning.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Обрабатываем клиента в отдельном потоке
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
                clientThreads.add(clientHandler);  // Добавляем поток в список
            } catch (IOException e) {
                if (isRunning.get()) {
                    System.err.println("Error while accepting client connection: " + e.getMessage());
                }
            }
        }

        // Ожидаем завершения всех клиентских потоков перед остановкой сервера
        for (Thread thread : clientThreads) {
            try {
                thread.join();  // Дожидаемся завершения каждого потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Закрытие серверного сокета после завершения работы всех клиентов
        serverSocket.close();
        System.out.println("Server stopped.");
    }

    // Метод для логирования сообщений
    public static void logMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Метод для остановки сервера
    public static void stopServer() {
        isRunning.set(false);  // Устанавливаем флаг в false, чтобы выйти из цикла
        try {
            serverSocket.close();  // Закрываем серверный сокет для того, чтобы выйти из accept
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

