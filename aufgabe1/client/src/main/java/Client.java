import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) {
        try (final Socket socket = new Socket("localhost", 7878)) {
            final BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            final InputStream inputStream = socket.getInputStream();
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connection established!");
            while (!socket.isInputShutdown() && !socket.isOutputShutdown()) {
                if (inputStream.available() > 0) {
                    // dangerous! we are checking whether bytes are available to read, not whether newline available
                    // -> possible deadlock
                    final String response = br.readLine();
                    System.out.println(response);
                    if (isTerminatingCommand(response)) {
                        break;
                    }
                }
                if (stdInReader.ready()) {
                    final String cmd = stdInReader.readLine();
                    dataOutputStream.write(cmd.concat("\n").getBytes(StandardCharsets.UTF_8));
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
        return response.equals("OK BYE") || response.equals("OK SHUTDOWN") || response.equals("ERROR OUT_OF_RESS") || response.equals("ERROR TERMINATING");
    }
}
