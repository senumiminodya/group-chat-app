package lk.ijse.groupChat.server;

import lk.ijse.groupChat.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {
    private ServerSocket serverSocket;
    private Socket socket;
    private static ServerHandler serverHandler;
    private List<ClientHandler> clients = new ArrayList<>();

    public ServerHandler() throws IOException {
        serverSocket = new ServerSocket(3001);
    }

    public static ServerHandler getInstance() throws IOException {
        return serverHandler == null ? serverHandler = new ServerHandler() : serverHandler;
    }

    public void makeSocket(){
        while (!serverSocket.isClosed()){
            try{
                socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket,clients);
                clients.add(clientHandler);
                System.out.println("client socket accepted "+socket.toString());
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
