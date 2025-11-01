package clientPackage;

import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddr = InetAddress.getByName(SERVER_ADDRESS);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine();

   
            Thread receiveThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n" + message);
                        System.out.print("> "); 
                    }
                } catch (Exception e) {
                    System.out.println("Connexion terminée.");
                }
            });
            receiveThread.start();

            System.out.println("Connecté au serveur. Tapez vos messages (exit pour quitter) :");

            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Déconnexion...");
                    socket.close();
                    break;
                }

                String fullMessage = "[" + pseudo + "] : " + message;
                byte[] sendData = fullMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length, serverAddr, SERVER_PORT
                );
                socket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


