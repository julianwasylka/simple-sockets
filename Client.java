package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Client.class.getName());

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Scanner scanner = new Scanner(System.in)
        ) {
            //logger.info("Polaczono z serwerem");

            String serverReady = (String)in.readObject();
            logger.info("Info od serwera  - " + serverReady);
            if ("ready".equals(serverReady)) {
                System.out.print("Podaj N - ");
                int number = scanner.nextInt();
                out.writeObject(number);
                out.flush();

                String serverReadyForMessages = (String)in.readObject();
                logger.info("Info od serwera  - " + serverReadyForMessages);
                if ("ready for messages".equals(serverReadyForMessages)) {
                    for (int i = 0; i < number; i++) {
                        System.out.print("Podaj wiad " + (i + 1) + " - ");
                        String mess = scanner.next();
                        Message message = new Message(i + 1, mess);
                        out.writeObject(message);
                        out.flush();
                    }

                    String finished = (String) in.readObject();
                }
                else {
                    logger.warning("Serwer nie potwierdzil wysylania");
                }
            }
            else {
                logger.warning("Serwer nie potwierdzil");
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Blad klienta - " + e.getMessage(), e);
        }
    }
}

