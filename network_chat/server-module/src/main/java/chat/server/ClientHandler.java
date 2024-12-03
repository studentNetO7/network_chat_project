package chat.server;

import java.io.*;
import java.net.*;


public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter outClient;
    private BufferedReader inClient;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outClient = new PrintWriter(clientSocket.getOutputStream(), true);

            clientName = inClient.readLine();
            if (clientName == null || clientName.isEmpty()) {
                clientName = "Anonymous";
            }

            // Добавляем клиента в список для рассылки сообщений
            MessageSender.addClient(outClient);

            String clientMessage;
            while ((clientMessage = inClient.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("/exit")) {
                    // Отправляем всем остальным клиентам уведомление о выходе клиента
                    MessageSender.notifyExit(clientName);
                    break; // Завершаем работу клиента
                }

                // Логируем и отправляем сообщение всем
                Server.logMessage(clientName + ": " + clientMessage);
                String messageToSend = clientName + ": " + clientMessage;
                MessageSender.senderToAll(messageToSend, outClient);
            }
        } catch (IOException e) {
            // Логируем ошибку с дополнительной информацией
            System.err.println("Error while reading message from client " + clientName + ": " + e.getMessage());
            e.printStackTrace();
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
                // Логируем ошибку при закрытии потоков и сокета
                System.err.println("Error while closing resources for client " + clientName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
