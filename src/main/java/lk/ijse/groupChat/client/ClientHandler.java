package lk.ijse.groupChat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private List<ClientHandler> clients;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String msg = "";

    //Constructor for initializing the client handler with a socket and a list of clients
    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        try {
            //Initialize instance variables with provided socket and clients list
            this.socket = socket;
            this.clients = clients;
            //Create input and output streams for communication with the client
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start a new thread to handle communication with the client
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Continuously listen for messages from the client, while the socket is connected
                    while (socket.isConnected()) {
                        //Read a UTF-encoded message from the client
                        msg = dataInputStream.readUTF();
                        //Broadcast the received message to all other clients
                        for (ClientHandler clientHandler : clients) {
                            //Avoid sending the message back to the original sender
                            if (clientHandler.socket.getPort() != socket.getPort()) {
                                //Send the message to the other client's data output stream
                                clientHandler.dataOutputStream.writeUTF(msg);
                                clientHandler.dataOutputStream.flush();
                            }
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
