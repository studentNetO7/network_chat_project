package chat.server;

import java.io.*;
import java.net.*;


public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private PrintWriter outClient;
    private BufferedReader inClient;
    private String clientName;
    private final String clientIpAddress;

    public ClientHandler(Socket socket, String clientIpAddress) {
        this.clientSocket = socket;
        this.clientIpAddress = clientIpAddress;
    }

    @Override
    public void run() {
        try {
            inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outClient = new PrintWriter(clientSocket.getOutputStream(), true);

            // Чтение имени клиента
            clientName = inClient.readLine();
            if (clientName == null || clientName.isEmpty()) {
                clientName = "Anonymous";
            }
            // Логируем подключение клиента с IP-адресом и именем
            Server.logMessage(clientName + " connected from /" + clientIpAddress);
            System.out.println("Client " + clientName + " connected from /" + clientIpAddress);

            // Добавляем клиента в список для рассылки сообщений
            MessageSender.addClient(outClient);

            String clientMessage;
            // Чтение сообщений от клиента
            while ((clientMessage = inClient.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("/exit")) {
                    // Логируем выход клиента
                    Server.logMessage(clientName + " has left the chat.");

                    // Отправляем всем остальным клиентам уведомление о выходе клиента
                    MessageSender.notifyExit(clientName);
                    break; // Завершаем работу клиента
                }

                // Логируем и отправляем сообщение всем
               Server.logMessage("Rcvd from "+ clientName + ": " + clientMessage);
                String messageToSend = clientName + ": " + clientMessage;
                MessageSender.senderToAll(messageToSend, outClient);
            }
        } catch (IOException e) {
            // Обрабатываем исключение и логируем ошибку при чтении сообщения от клиента
            String errorMessage = "Error while reading message from client ";
            Server.logMessage(errorMessage);  // Логируем ошибку в файл
            System.err.println(errorMessage);
            e.printStackTrace();  // Печатаем стек ошибки в консоль
        } finally {
            try {
                // Убираем клиента из списка рассылки при его отключении
                if (outClient != null) {
                    MessageSender.removeClient(outClient);
                }

                // Закрытие потоков и сокета
                if (inClient != null) {
                    inClient.close();
                }
                if (outClient != null) {
                    outClient.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Сообщаем и логируем ошибку при закрытии потоков и сокета
                String closeErrorMessage = "Error while closing resources for client " + clientName + ": " + e.getMessage();
                Server.logMessage(closeErrorMessage);  // Логируем ошибку в файл
                System.err.println(closeErrorMessage);
                e.printStackTrace();  // Печатаем стек ошибки в консоль
            }
        }
    }
}
