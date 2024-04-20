import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) {
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        try (Socket clientSocket = new Socket(address, port);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))){

            String command;
            String response = inFromServer.readLine();
            if (response != null) throw new IOException("Too many clients");

            do {
                command = userInput.readLine();
                outToServer.writeBytes(command/* + '\n'*/); //alternativ: writeUTF (?)
                response = inFromServer.readLine();
                System.out.println(response);
            } while (!response.equals("OK BYE") && !response.equals("OK SHUTDOWN"));

            //clientSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
