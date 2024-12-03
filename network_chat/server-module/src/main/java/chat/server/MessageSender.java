package chat.server;

import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArraySet;

public class MessageSender {
    // Сет для хранения всех участников чата (PrintWriter) клиентов
    private static final CopyOnWriteArraySet<PrintWriter> clientWriters = new CopyOnWriteArraySet<>();

    // Метод для добавления нового клиента
    public static void addClient(PrintWriter writer) {
        clientWriters.add(writer);
    }

    // Метод для удаления клиента
    public static void removeClient(PrintWriter writer) {
        clientWriters.remove(writer);
    }

    // Метод для рассылки сообщений всем подключённым клиентам, кроме самого отправителя
    public static void senderToAll(String message, PrintWriter senderWriter) {
        for (PrintWriter writer : clientWriters) {
            if (writer != senderWriter) {  // Проверяем, что это не отправитель
                try {
                    writer.println(message);  // Отправляем сообщение каждому клиенту
                } catch (Exception e) {
                    // Обработка исключений (например, удаление клиента)
                    clientWriters.remove(writer);
                }
            }
        }
    }

    // Метод для отправки уведомления об выходе клиента
    public static void notifyExit(String clientName) {
        String exitMessage = clientName + " has left the chat.";
        synchronized (clientWriters) {
            // Отправляем всем клиентам уведомление
            for (PrintWriter writer : clientWriters) {
                try {
                    writer.println(exitMessage);  // Уведомляем всех клиентов
                } catch (Exception e) {
                    // Если произошла ошибка, например, если клиент отключился, удаляем его
                    clientWriters.remove(writer);
                }
            }
        }
    }

}
