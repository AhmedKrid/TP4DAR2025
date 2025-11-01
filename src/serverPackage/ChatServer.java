package serverPackage;

import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    // Liste pour stocker les clients connectés
    private static List<InetSocketAddress> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            byte[] buffer = new byte[BUFFER_SIZE];
            System.out.println("Serveur UDP multi-clients démarré sur le port " + PORT + "...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetSocketAddress sender = new InetSocketAddress(packet.getAddress(), packet.getPort());

                if (!clients.contains(sender)) {
                    clients.add(sender);
                    System.out.println("Nouveau client connecté : " + sender);
                }

                System.out.println("Message reçu de " + sender + " → " + message);

                for (InetSocketAddress client : clients) {
                    if (!client.equals(sender)) {
                        byte[] sendData = message.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(
                                sendData, sendData.length,
                                client.getAddress(), client.getPort()
                        );
                        socket.send(sendPacket);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
