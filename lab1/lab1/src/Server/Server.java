package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static LinkedList<RecIntegral> tableData = new LinkedList<>();
    private static List<InetSocketAddress> clientAddresses = new ArrayList<>();
    private static DatagramSocket socket;

    public static void main(String[] args) {
        int port = 4540;
        try {
            socket = new DatagramSocket(port);
            System.out.println("Server started on port: " + port);
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                InetSocketAddress clientSocketAddress = new InetSocketAddress(clientAddress, clientPort);
                

                if (!clientAddresses.contains(clientSocketAddress)) {
                    clientAddresses.add(clientSocketAddress);
                    System.out.println("User " + clientAddress.getHostAddress() + " connected to server.");

                }

                byte[] sendData = new byte[1024];
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);

                // Handle received data
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(receiveData));
                Object obj = in.readObject();
                if (obj instanceof LinkedList<?>) {
                    LinkedList<RecIntegral> newData = (LinkedList<RecIntegral>) obj;
                    synchronized (tableData) {
                        tableData.addAll(newData);
                        System.out.println(tableData.size());
                    }

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(outputStream);
                    out.writeObject(newData);
                    sendData = outputStream.toByteArray();
                }

                // Send data to all clients
                for (InetSocketAddress address : clientAddresses) {
                    sendPacket.setSocketAddress(address);
                    sendPacket.setData(sendData);
                    socket.send(sendPacket);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
