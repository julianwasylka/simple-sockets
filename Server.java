package org.example;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Server.class.getName());

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Serwer odpalony, port - " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nowy klient - " + clientSocket.toString());

                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Blad serwera - " + e.getMessage(), e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Logger logger;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.logger = Logger.getLogger(ClientHandler.class.getName());
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
            ) {
                out.writeObject("ready");
                out.flush();

                int number = (int)in.readObject();
                logger.info("N - " + number);

                out.writeObject("ready for messages");
                out.flush();

                for (int i = 0; i < number; i++) {
                    Message message = (Message)in.readObject();
                    logger.info("Doszla wiadomosc - " + message.getContent() + " od klienta - " + clientSocket.toString());
                }

                out.writeObject("finished");
                out.flush();

            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Blad polaczenia - " + e.getMessage(), e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Blad w zamykaniu - " + e.getMessage(), e);
                }
            }
        }
    }
}
