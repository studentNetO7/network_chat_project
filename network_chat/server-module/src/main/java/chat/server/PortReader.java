package chat.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class PortReader {
    private static final String SETTINGS_FILE = "settings.txt";

    public static int readPort() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            String portString = reader.readLine();
            if (portString != null && !portString.isEmpty()) {
                try {
                    return Integer.parseInt(portString);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number in settings.txt. Using default port.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading settings file. Using default port.");
        }
        return 12345; // Порт по умолчанию
    }
}
