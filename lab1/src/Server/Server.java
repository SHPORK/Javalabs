package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static LinkedList<RecIntegral> tableData = new LinkedList<>();
    private static List<ClientHandler> clientHandlers = new ArrayList<>();
    
    public static void main(String[] args) {
        int port = 4540;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("User " + clientSocket.getInetAddress().getHostName() + " connected to server.");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler); // Add the new client handler to the list
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof LinkedList<?>) {
                        LinkedList<RecIntegral> newData = (LinkedList<RecIntegral>) obj;
                        synchronized (tableData) {
                            tableData.addAll(newData);
                            System.out.println(tableData.size());
                        }
                        
                        synchronized (clientHandlers) {
                            for (ClientHandler handler : clientHandlers) {
                                handler.sendData(newData);
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostName());
            } finally {
                
                try {
                    clientSocket.close();
                    clientHandlers.remove(this); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

      
        public void sendData(LinkedList<RecIntegral> newData) throws IOException {
            out.writeObject(newData);
            out.flush();
        }
    }

}
