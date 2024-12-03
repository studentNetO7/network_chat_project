package chat.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PortReader {
    private static final String SETTINGS_FILE = "settings.txt";

    public static int readPort() {
        // Открываем файл настроек для чтения
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            // Читаем первую строку из файла, предполагая, что в ней хранится номер порта
            String portString = reader.readLine();
            // Проверяем, если строка не пустая и не равна null, пытаемся преобразовать ее в целое число
            if (portString != null && !portString.isEmpty()) {
                try {
                    // Преобразуем строку в целое число (номер порта)
                    return Integer.parseInt(portString);
                    // Если строка не является числом, выводим ошибку и используем порт по умолчанию
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number in settings.txt. Using default port.");
                }
            }
            // Если возникла ошибка при чтении файла (например, файл не существует или поврежден), выводим ошибку и используем порт по умолчанию
        } catch (IOException e) {
            System.out.println("Error reading settings file. Using default port.");
        }
        return 12345; // Порт по умолчанию
    }
}
