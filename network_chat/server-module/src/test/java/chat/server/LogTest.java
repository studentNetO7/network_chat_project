package chat.server;

import chat.server.Server;
import chat.server.ClientHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.*;

import static org.junit.Assert.*;

public class LogTest {

    private Thread serverThread;
    private static final int TEST_PORT = 12345;
    private static final String SERVER_HOST = "localhost";
    private final boolean serverRunning = false;

    // Запуск сервера в фоновом потоке
    @Before
    public void setUp() throws Exception {
        serverThread = new Thread(() -> {
            try {
                Server.main(new String[0]);  // Запускаем сервер
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Даем серверу больше времени для старта
        Thread.sleep(2000);  // Увеличиваем время ожидания

        // Дополнительная проверка, чтобы убедиться, что сервер действительно работает
        Socket testSocket = null;
        try {
            testSocket = new Socket("localhost", TEST_PORT);  // Проверка доступности порта
        } catch (IOException e) {
            fail("Server didn't start within expected time");  // Если сервер не запустился
        } finally {
            if (testSocket != null) {
                testSocket.close();
            }
        }
    }

    // Завершаем работу сервера после теста
    @After
    public void tearDown() throws Exception {
        Server.stopServer();  // Останавливаем сервер
        serverThread.join();   // Ожидаем завершения потока
    }
    // Проверка доступности порта
    private boolean isServerRunning(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true; // Если подключение успешно, сервер работает
        } catch (IOException e) {
            return false; // Сервер не доступен
        }
    }

    // Тестирование соединения и отправки сообщений
    @Test
    public void testServerAndClientInteraction() throws IOException {
        // Клиент 1 подключается
        Socket clientSocket1 = new Socket(SERVER_HOST, TEST_PORT);
        PrintWriter outClient1 = new PrintWriter(clientSocket1.getOutputStream(), true);
        BufferedReader inClient1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

        // Клиент 2 подключается
        Socket clientSocket2 = new Socket(SERVER_HOST, TEST_PORT);
        PrintWriter outClient2 = new PrintWriter(clientSocket2.getOutputStream(), true);
        BufferedReader inClient2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));

        // Ожидаем запроса имени для клиента 1
        assertEquals("Enter your name: ", inClient1.readLine());
        outClient1.println("Client1");

        // Ожидаем запроса имени для клиента 2
        assertEquals("Enter your name: ", inClient2.readLine());
        outClient2.println("Client2");

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

        // Закрываем соединения
        clientSocket1.close();
        clientSocket2.close();
    }
}
