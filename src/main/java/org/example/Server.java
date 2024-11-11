package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;


public class Server {
    private static final int PORT = 7007;  // Port to listen on
    private static Set<ClientHandler> clientHandlers = new HashSet<>();  // To store connected clients
    private static ExecutorService threadPool = Executors.newCachedThreadPool();  // Thread pool to handle multiple clients

    public static void main(String[] args) {
        System.out.println("Chat server started...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Wait for a client to connect
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create a new ClientHandler to handle this client in a new thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);

                // Start the thread to handle client communication
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a message to all connected clients except the sender
    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != sender) {
                    clientHandler.sendMessage(message);  // Send the message to the other clients
                }
            }
        }
    }

    // Remove a client from the set of connected clients
    public static void removeClient(ClientHandler clientHandler) {
        synchronized (clientHandlers) {
            clientHandlers.remove(clientHandler);
        }
    }

    // ClientHandler class that manages communication with a single client
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Ask the client for their username
                out.println("Welcome to the chatroom! Please enter your name:");
                this.userName = in.readLine();  // Read the username from the client

                // Broadcast the welcome message to all clients
                broadcast(userName + " has joined the chat!", this);

                String message;
                while ((message = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(message)) {
                        break;  // Exit the chat if the user types "exit"
                    }

                    // Broadcast the message to all other clients
                    broadcast(userName + ": " + message, this);
                }

                // Remove the client from the list and close the connection
                out.println("Goodbye " + userName + "!");
                broadcast(userName + " has left the chat.", this);
                removeClient(this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Send a message to this client
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}