package lk.ijse.groupChat.server;

import lk.ijse.groupChat.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {
    private ServerSocket serverSocket; // ServerSocket for handling client connections
    private Socket socket; // Socket to accept incoming client connections
    private static ServerHandler serverHandler; // Singleton instance of ServerHandler
    private List<ClientHandler> clients = new ArrayList<>(); // List to keep track of connected clients

    // Private constructor to create a ServerHandler with a ServerSocket
    private ServerHandler() throws IOException {
        serverSocket = new ServerSocket(3001);
    }

    // Singleton pattern to get an instance of ServerHandler
    public static ServerHandler getInstance() throws IOException {
        return serverHandler == null ? serverHandler = new ServerHandler() : serverHandler;
    }

    // Method to create sockets and handle incoming connections
    public void makeSocket(){
        while (!serverSocket.isClosed()){
            try{
                // Accept an incoming client connection
                socket = serverSocket.accept();
                // Create a ClientHandler for the new client
                ClientHandler clientHandler = new ClientHandler(socket,clients);
                // Add the client to the list of connected clients
                clients.add(clientHandler);
                System.out.println("client socket accepted "+socket.toString());
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
