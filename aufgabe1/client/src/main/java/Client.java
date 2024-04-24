import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) {
        final Dotenv dotenv = Dotenv.load();
        final int bytePerPacket = Integer.parseInt(dotenv.get("BYTE_PER_PAKET"));
        try (final Socket socket = new Socket("localhost", 7878)) {
            socket.setSendBufferSize(bytePerPacket);
            final BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            final InputStream inputStream = socket.getInputStream();
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final OutputStream outputStream = socket.getOutputStream();
            System.out.println("Connection established!");
            while (!socket.isInputShutdown() && !socket.isOutputShutdown()) {
                if (inputStream.available() > 0) {
                    final String response = br.readLine();
                    System.out.println(response);
                    if (isTerminatingCommand(response)) {
                        break;
                    }
                }
                if (stdInReader.ready()) {
                    final String cmd = stdInReader.readLine();
                    outputStream.write(cmd.concat("\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            System.out.println("Connection closed.");
        } catch (UnknownHostException e) {
            System.err.println("Could not resolve Host");
        } catch (IOException e) {
            System.err.println("Could not open Connection");
        }
    }

    private static boolean isTerminatingCommand(String response) {
        return response.equals("OK BYE") || response.equals("OK SHUTDOWN") || response.equals("ERROR SERVER_CLOSING") || response.equals("ERROR NO_CONN_AVAIL");
    }
}
