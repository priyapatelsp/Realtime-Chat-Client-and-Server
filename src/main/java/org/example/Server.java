package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Server {
    public static void main(String[] args) {
        int port = 7007;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Echo server is listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println("Received from client: " + message);
                    output.println(message);  // Echo the message back to the client
                }

                input.close();
                output.close();
                clientSocket.close();
                System.out.println("Client disconnected.");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}