package chat.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {

    @Test
    public void testServerConnection() throws IOException {
        // Настроим сервер
        int port = 12345;
        ServerSocket serverSocket = new ServerSocket(port);

        // Проверим, что сервер может принимать соединения
        Socket clientSocket = new Socket("localhost", port);

        // Соединение установлено, поэтому проверим, что оно не null
        assertNotNull(clientSocket);

        clientSocket.close();
        serverSocket.close();
    }
}


