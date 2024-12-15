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
    // Используем коллекцию для отслеживания активных клиентских потоков (в случае реализации закрытия сервера)
    //private static List<Thread> clientThreads = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int PORT = PortReader.readPort();
        try {
            //private static AtomicBoolean isRunning = new AtomicBoolean(true);
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            // Ожидаем подключения клиентов
            // while (isRunning.get()) { - это в случае использования флага (для реализации закрытия сервера)
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String clientIpAddress = clientSocket.getInetAddress().getHostAddress();

                    // Создаем клиентский обработчик, который обрабатывает клиента в отдельном потоке и будет ожидать имени от клиента
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientIpAddress);
                    clientHandler.start();
                    //clientThreads.add(clientHandler);  // Добавляем поток в список (для реализации закрытия сервера)
                } catch (IOException e) {
                    //if (isRunning.get()) { (для реализации закрытия сервера)
                    logMessage("Error while accepting client connection: " + e.getMessage());
                    System.err.println("Error while accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logMessage("Server socket creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

//        // Ожидаем завершения всех клиентских потоков перед остановкой сервера
//        for (Thread thread : clientThreads) {
//            try {
//                thread.join();  // Дожидаемся завершения каждого потока
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

//        // Закрытие серверного сокета после завершения работы всех клиентов
//        serverSocket.close();
//        stopServer();  // После завершения работы всех клиентов останавливаем сервер
//        System.out.println("Server stopped.");

    // Метод для логирования сообщений
    public static void logMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            // Логируем сообщение с пометкой [SERVER]
            writer.write("[" + timestamp + "] [SERVER] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    // Метод для остановки сервера
//    public static void stopServer() {
//        isRunning.set(false);
//        try {
//            if (clientThreads.isEmpty()) {
//                serverSocket.close();  // Закрываем серверный сокет
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

