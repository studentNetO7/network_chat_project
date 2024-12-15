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

        // Получение порта через PortReader
        int port = PortReader.readPort();
        // Подключение к серверу
        Socket socket = new Socket("localhost", port);
        PrintWriter outClient = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Логирование соединения с сервером
        logMessage(name + " is connecting");
        // Отправляем имя клиента на сервер
        outClient.println(name);
        // Клиент приглашается к началу общения в чате
        System.out.print(name + ", write here for starting conversation: ");
        System.out.println();

        // Поток для получения сообщений от сервера
        Thread receiveThread = new Thread(() -> {
            try {
                String serverMessage;
                while (!Thread.currentThread().isInterrupted() && (serverMessage = inClient.readLine()) != null) {
                    // Печатаем и логируем сообщения от других клиентов
                    System.out.println(serverMessage);
                    logMessage(name + " Rcvd: " + serverMessage);
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        // Поток для отправки сообщений на сервер
        Thread sendThread = new Thread(() -> {
            try {
                String message;
                while (true) {
                    message = reader.readLine();
                    if (message.equalsIgnoreCase("/exit")) {
                        outClient.println("/exit");  // Отправляем команду на сервер
                        break;
                    }
                    // Отправляем сообщение и логируем его
                    logMessage(name + " Sent: " + message);
                    outClient.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Запуск потоков
        receiveThread.start();  // Поток для получения сообщений
        sendThread.start();  // Поток для отправки сообщений

        // Ждем завершения потока отправки
        try {
            sendThread.join();  // Ждем завершения потока отправки
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Завершаем работу потока получения сообщений
        try {
            receiveThread.interrupt();  // Останавливаем поток получения сообщений
            inClient.close();
            outClient.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для логирования сообщений
    private static void logMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            // Логируем сообщение с пометкой [CLIENT]
            writer.write("[" + timestamp + "] [CLIENT] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
