import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        try {
            //Establish connection
            String address = args[0];
            int port = Integer.parseInt(args[1]);
            Socket socket = new Socket(address, port);
            System.out.println("Connected!");

            //TODO: Welcome Message (User Guide)

            //Input & Output (hier als String, evtl. nicht der Aufgabenstellung entsprechend, Alternative: DataInputStream?)
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String command;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String response;

            do {
                command = userInput.readLine();
                out.println(command);
                response = in.readLine();
                System.out.println(response);
            } while (!response.equals("OK BYE") && !response.equals("OK SHUTDOWN"));

        } catch (Exception e) {
            //TODO: handle exception
            //e.printStackTrace();
            System.out.println(e);
        }
    }
}
