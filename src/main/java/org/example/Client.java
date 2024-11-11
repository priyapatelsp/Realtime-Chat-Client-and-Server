package org.example;

import java.io.*;
import java.net.*;

public class Client {
    private static String userName;
    private static Socket socket;
    private static BufferedReader serverInput;
    private static PrintWriter serverOutput;
    private static BufferedReader userInput;

    public static void main(String[] args) {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your name: ");
            userName = consoleReader.readLine();

            String serverAddress = "127.0.0.1";
            int port = 7007;
            socket = new Socket(serverAddress, port);
            System.out.println("Connected to server at " + serverAddress + ":" + port);

            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            Thread receiveThread = new Thread(new ReceiveMessages());
            receiveThread.start();
            String message;
            while (true) {
                System.out.print("You: ");
                message = userInput.readLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                serverOutput.println(userName + ": " + message);
                System.out.println(userName + ": " + message); // Echo locally
            }

            serverInput.close();
            serverOutput.close();
            userInput.close();
            socket.close();
            System.out.println("Chat ended.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static class ReceiveMessages implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = serverInput.readLine()) != null) {
                    System.out.println(serverMessage);  // Print the message from the server
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }
    }
}
