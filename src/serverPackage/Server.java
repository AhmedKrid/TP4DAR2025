package serverPackage;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 1234;
    private static AtomicInteger totalOperations = new AtomicInteger(0);
    private static AtomicInteger clientCount = new AtomicInteger(0);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("======================================");
            System.out.println("Serveur multi-thread TCP démarré !");
            System.out.println("Adresse : " + InetAddress.getLocalHost().getHostAddress() + " | Port : " + PORT);
            System.out.println("======================================");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                int clientNumber = clientCount.incrementAndGet();
                System.out.println("[+] Nouveau client connecté : " + clientSocket.getRemoteSocketAddress()
                        + " | Client n°" + clientNumber);

                // Start a thread for each client
                new Thread(new ClientHandler(clientSocket, clientNumber)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle each client
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private int clientNumber;

        public ClientHandler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("Bienvenue ! Vous êtes le client n°" + clientNumber);

                Object obj;
                while ((obj = in.readObject()) != null) {
                    if (obj instanceof String && ((String) obj).equalsIgnoreCase("exit")) {
                        break;
                    }

                    if (obj instanceof Operation) {
                        Operation op = (Operation) obj;
                        double resultValue = 0;
                        String message;

                        try {
                            switch (op.getOperator()) {
                                case "+": resultValue = op.getNumber1() + op.getNumber2(); break;
                                case "-": resultValue = op.getNumber1() - op.getNumber2(); break;
                                case "*": resultValue = op.getNumber1() * op.getNumber2(); break;
                                case "/":
                                    if (op.getNumber2() == 0) throw new ArithmeticException("Division par zéro");
                                    resultValue = op.getNumber1() / op.getNumber2();
                                    break;
                                default: throw new Exception("Opérateur invalide");
                            }
                            // Increment the global counter
                            int total = totalOperations.incrementAndGet();
                            System.out.println("Opération traitée : Client n°" + clientNumber
                                    + " | Total opérations : " + total);

                            message = "Résultat = " + resultValue;
                        } catch (Exception e) {
                            message = "Erreur : " + e.getMessage();
                        }

                        out.writeObject(new Result(resultValue, message));
                    }
                }

                System.out.println("[-] Client n°" + clientNumber + " déconnecté : " + socket.getRemoteSocketAddress());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
