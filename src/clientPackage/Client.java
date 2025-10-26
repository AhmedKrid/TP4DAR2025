package clientPackage;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import serverPackage.Operation;
import serverPackage.Result;

public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Entrez l'adresse IP du serveur : ");
        String ipServeur = sc.nextLine();
        int port = 1234;

        try (Socket socket = new Socket(ipServeur, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println(in.readObject()); // welcome message

            while (true) {
                System.out.print("Entrez une opération (ex: 5 + 2) ou 'exit' : ");
                String input = sc.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    out.writeObject("exit");
                    break;
                }

                String[] tokens = input.trim().split(" ");
                if (tokens.length != 3) {
                    System.out.println("Format invalide, utilisez : nombre opérateur nombre");
                    continue;
                }

                try {
                    double num1 = Double.parseDouble(tokens[0]);
                    String operator = tokens[1];
                    double num2 = Double.parseDouble(tokens[2]);

                    Operation op = new Operation(num1, operator, num2);
                    out.writeObject(op);

                    Result result = (Result) in.readObject();
                    System.out.println("Réponse du serveur -> " + result.getMessage());

                } catch (NumberFormatException e) {
                    System.out.println("Erreur : les nombres doivent être valides.");
                }
            }

            System.out.println("Client terminé.");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
