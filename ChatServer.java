import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1024)) {
            System.out.println("Server is running and listening on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                Thread clientThread = new Thread(new ClientHandler(clientSocket, writer));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientWriter;
        private String clientName;
    
        public ClientHandler(Socket socket, PrintWriter writer) {
            clientSocket = socket;
            clientWriter = writer;
        }
    
        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                // Request and read client's name
                clientName = reader.readLine();
                System.out.println(clientName + " connected.");
    
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcast(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientWriters.remove(clientWriter);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    
        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                if (writer != clientWriter) {
                    writer.println(message);
                }
            }
        }
    }
}

