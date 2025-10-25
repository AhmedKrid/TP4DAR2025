package clientPackage;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * TP3 - Client TCP
 * Développement d'applications réparties - LSI3
 * Université de Sfax
 * Auteur : [Votre Nom et Prénom]
 */
public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Entrez l'adresse IP du serveur : ");
        String serverIp = sc.nextLine();
        int port = 1234;

        try (
            Socket socket = new Socket(serverIp, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Connecté au serveur !");
            System.out.println(in.readLine()); // Message de bienvenue

            while (true) {
                System.out.print("Entrez une opération (ex: 4 * 7) ou 'exit' pour quitter : ");
                String operation = sc.nextLine();
                out.println(operation);

                if (operation.equalsIgnoreCase("exit")) {
                    System.out.println("Fermeture du client...");
                    break;
                }

                String response = in.readLine();
                if (response == null) break;
                System.out.println("Réponse du serveur -> " + response);
            }

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
