import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1024 );
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.print("Enter your name: ");
            String userName = scanner.nextLine();
            writer.println(userName);
            System.out.println("Connected to server.");

            Thread messageThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageThread.start();

            while (true) {
                String userInput = scanner.nextLine();
                writer.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
