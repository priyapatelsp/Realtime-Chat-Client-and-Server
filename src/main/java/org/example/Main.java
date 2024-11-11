package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello welcome to server ");
        int portNumber=7007;
        try(ServerSocket serverSocket=new ServerSocket(portNumber)){
            System.out.println("Echo server started on PORT :"+portNumber);
            System.out.println("Waiting for connection .... ");
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client ready -"+clientSocket.getInetAddress());
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer = new PrintWriter(outputStream, true);
                String message;

                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    writer.println(message);  // Echo the message back to the client
                }


            }catch (IOException e){
                System.err.println("Error while handling client connection: " + e.getMessage());
            }

        }catch (Exception e){
            System.err.println("Error in connection: " + e.getMessage());
        }
    }
}