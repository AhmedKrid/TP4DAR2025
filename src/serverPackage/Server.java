package serverPackage;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TP3 - Serveur multi-thread
 * Développement d'applications réparties - LSI3
 * Université de Sfax
 * Auteur : [Votre Nom et Prénom]
 */
public class Server {
    // Compteur global pour numéroter les clients
    private static AtomicInteger clientCount = new AtomicInteger(0);

    public static void main(String[] args) {
        int port = 1234; // conforme à l'énoncé (telnet localhost 1234)

        try {
            InetAddress ip = InetAddress.getLocalHost();
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));

            System.out.println("======================================");
            System.out.println("Serveur multi-thread TCP démarré !");
            System.out.println("Adresse : " + ip.getHostAddress() + " | Port : " + port);
            System.out.println("======================================");

            // Attente continue des connexions clients
            while (true) {
                Socket clientSocket = serverSocket.accept();

                int clientNumber = clientCount.incrementAndGet();
                String clientAddress = clientSocket.getRemoteSocketAddress().toString();

                System.out.println("[+] Nouveau client connecté : " + clientAddress + " | Client n°" + clientNumber);

                // Création d’un thread pour ce client
                Thread t = new Thread(new ClientHandler(clientSocket, clientNumber));
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }
}

/**
 * Classe de gestion d’un client (thread indépendant)
 */
class ClientHandler implements Runnable {
    private Socket socket;
    private int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Message de bienvenue au client
            out.println("Bienvenue ! Vous êtes le client n°" + clientNumber);

            String input;
            while ((input = in.readLine()) != null) {
                input = input.trim();
                if (input.equalsIgnoreCase("exit")) {
                    out.println("Déconnexion du serveur...");
                    break;
                }

                try {
                    // Format attendu : nombre opérateur nombre
                    String[] tokens = input.split(" ");
                    if (tokens.length != 3) throw new Exception("Format invalide");

                    double n1 = Double.parseDouble(tokens[0]);
                    String op = tokens[1];
                    double n2 = Double.parseDouble(tokens[2]);

                    double res = switch (op) {
                        case "+" -> n1 + n2;
                        case "-" -> n1 - n2;
                        case "*" -> n1 * n2;
                        case "/" -> {
                            if (n2 == 0) throw new Exception("Division par zéro");
                            yield n1 / n2;
                        }
                        default -> throw new Exception("Opérateur invalide");
                    };

                    out.println("Résultat = " + res);
                } catch (Exception e) {
                    out.println("Erreur : opération invalide (ex: 5 + 2)");
                }
            }

            System.out.println("[-] Client n°" + clientNumber + " déconnecté : "
                    + socket.getRemoteSocketAddress());
            socket.close();

        } catch (IOException e) {
            System.out.println("Erreur avec le client n°" + clientNumber + " : " + e.getMessage());
        }
    }
}
