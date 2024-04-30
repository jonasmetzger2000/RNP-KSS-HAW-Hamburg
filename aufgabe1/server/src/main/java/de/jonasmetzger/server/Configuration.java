package de.jonasmetzger.server;

import io.github.cdimascio.dotenv.Dotenv;

public class Configuration {
    public static String TCP_HOST;
    public static int TCP_PORT;
    public static int MAX_CLIENTS;
    public static int PROT_MAX_BYTE_SIZE;
    public static int TERMINATION_SOCKET_TIMEOUT;
    public static String SHUTDOWN_PASSWORD;

    private static Dotenv dotenv;

    public static void load() {
        dotenv = Dotenv.load();
        TCP_HOST = dotenv.get("TCP_HOST", "localhost");
        TCP_PORT = loadInt("TCP_PORT", 7878);
        MAX_CLIENTS = loadInt("MAX_CLIENTS", 3);
        PROT_MAX_BYTE_SIZE = loadInt("PROT_MAX_BYTE_SIZE", 255);
        TERMINATION_SOCKET_TIMEOUT = loadInt("TERMINATION_SOCKET_TIMEOUT", 30);
        SHUTDOWN_PASSWORD = dotenv.get("SHUTDOWN_PASSWORD", "HAW");
    }

    private static int loadInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(dotenv.get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
