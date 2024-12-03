package chat.server;

import chat.server.Server;
import chat.server.ClientHandler;
import chat.server.PortReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatIntegrationTest {

    private Thread serverThread;
    private static final int TEST_PORT = 12345;

    // Запуск сервера в фоновом потоке
    @BeforeEach
    public void setUp() throws Exception {
        serverThread = new Thread(() -> {
            try {
                Server.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Даем серверу время для старта
        Thread.sleep(1000);
    }

    // Завершаем работу сервера после теста
    @AfterEach
    public void tearDown() throws Exception {
        // Остановка сервера (добавляем специальный флаг для остановки)
        Server.stopServer();
        // Завершаем поток сервера
        serverThread.interrupt();
    }

    // Тестирование соединения и отправки сообщений
    @Test
    public void testServerAndClientInteraction() throws IOException, InterruptedException {
        // Клиент 1 подключается
        Socket clientSocket1 = new Socket("localhost", TEST_PORT);
        PrintWriter outClient1 = new PrintWriter(clientSocket1.getOutputStream(), true);
        BufferedReader inClient1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

        // Клиент 2 подключается
        Socket clientSocket2 = new Socket("localhost", TEST_PORT);
        PrintWriter outClient2 = new PrintWriter(clientSocket2.getOutputStream(), true);
        BufferedReader inClient2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));

        // Ожидаем запроса имени для клиента 1
        outClient1.println("Client1");
        // Ожидаем запроса имени для клиента 2
        outClient2.println("Client2");

        // Даем время для обработки и подключения
        Thread.sleep(500); // Задержка для того, чтобы клиенты успели подключиться и сервер обработал их

        // Отправляем сообщение от клиента 1
        String messageFromClient1 = "Hello from Client1!";
        outClient1.println(messageFromClient1);

        // Проверяем, что сообщение получено клиентом 2
        assertEquals("Client1: " + messageFromClient1, inClient2.readLine());

        // Отправляем сообщение от клиента 2
        String messageFromClient2 = "Hello from Client2!";
        outClient2.println(messageFromClient2);

        // Проверяем, что сообщение получено клиентом 1
        assertEquals("Client2: " + messageFromClient2, inClient1.readLine());

        // Даем немного времени, чтобы завершение соединений прошло корректно
        Thread.sleep(500); // Даем время для завершения обработки сообщений

        // Закрываем соединения
        clientSocket1.close();
        clientSocket2.close();
    }
}
