package chat.server;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LogTest {

    // Путь к лог-файлу
    private static final String LOG_FILE = "file.log";

    // Тестирование логирования сообщений
    @Test
    public void testLogMessage() throws IOException {
        // Пример сообщения для логирования
        String message = "Test message";

        // Логируем сообщение
        Server.logMessage(message);

        // Проверяем, что сообщение записано в файл
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String lastLine = null;
            String line;

            // Чтение файла построчно, чтобы получить последнюю строку
            while ((line = reader.readLine()) != null) {
                lastLine = line; // Запоминаем последнюю строку
            }

            // Проверяем, что последняя строка содержит логированное сообщение
            assertNotNull("Log file is empty", lastLine);
            assertTrue("Logged message is not found in the last line of the log",
                    lastLine.contains(message));
        }
    }
}
