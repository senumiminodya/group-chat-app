package lk.ijse.groupChat.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.groupChat.emaji.EmojiPicker;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientFormController {
    @FXML
    private JFXButton attachedBtn;

    @FXML
    private JFXButton imojiBtn;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXButton sendBtn;

    @FXML
    private Text txtLabel;

    @FXML
    private TextField txtMsg;

    @FXML
    private VBox vBox;
    public AnchorPane pane;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private String clientName = "client";

    public void initialize() {
        txtLabel.setText(clientName);

        // Start a new thread for handling socket communication
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    // Connect to the server
                    socket = new Socket("localhost", 3001);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");
                    // Notify the server that the client joined
                    ServerFormController.receiveMessage(clientName+" joined.");

                    // Keep listening for incoming messages
                    while (socket.isConnected()){
                        /*String receivingMsg = dataInputStream.readUTF();
                        // Process the received message
                        receiveMessage(receivingMsg, ClientFormController.this.vBox);
                        receiveImage(receivingMsg, ClientFormController.this.vBox);*/
                        String receivingMsg = dataInputStream.readUTF();

                        // Process the received message
                        if (receivingMsg.matches(".*\\.(png|jpe?g|gif)$")) {
                            // If the message is an image
                            receiveImage(receivingMsg, ClientFormController.this.vBox);
                        } else {
                            // If the message is a regular text message
                            receiveMessage(receivingMsg, ClientFormController.this.vBox);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        // Listener to scroll to the bottom when the height of the VBox changes
        this.vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });

        // Initialize emoji functionality
        emoji();
    }

    // Event handler for the attached button
    @FXML
    void attachedBtnOnAction(ActionEvent event) {
        /*FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory()+dialog.getFile();
        dialog.dispose();
        sendImage(file);
        System.out.println(file + " chosen.");*/
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Open");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            sendImage(file);
            System.out.println(file.getAbsolutePath() + " chosen.");
        }
    }

    @FXML
    void imojiBtnOnAction(ActionEvent event) {

    }

    // Method to set up and handle emojies
    private void emoji() {
        // Create the EmojiPicker
        EmojiPicker emojiPicker = new EmojiPicker();

        // Create a VBox to hold the emoji picker
        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(150,300);
        vBox.setLayoutX(400);
        vBox.setLayoutY(175);
        vBox.setStyle("-fx-font-size: 30");

        // Add the emoji picker to the main pane
        pane.getChildren().add(vBox);

        // Set the emoji picker as hidden initially
        emojiPicker.setVisible(false);

        // Show the emoji picker when the button is clicked
        imojiBtn.setOnAction(event -> {
            if (emojiPicker.isVisible()){
                emojiPicker.setVisible(false);
            }else {
                emojiPicker.setVisible(true);
            }
        });

        // Set the selected emoji from the picker to the text field (Used lambda expressions here)
        emojiPicker.getEmojiListView().setOnMouseClicked(event -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                txtMsg.setText(txtMsg.getText()+selectedEmoji);
            }
            emojiPicker.setVisible(false);
        });
    }

    // Method to send an image to the server
    private void sendImage(File file) {
        /*Image image = new Image(msgToSend);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
//        TextFlow textFlow = new TextFlow(imageView);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().add(hBox);

        try {
            dataOutputStream.writeUTF(clientName + "-" +msgToSend);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().add(hBox);

        try {
            dataOutputStream.writeUTF(clientName + "-" + file.getAbsolutePath());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Event handler for the send button
    @FXML
    void sendBtnOnAction(ActionEvent event) {
        // Send the entered message to the server
        sendMsg(txtMsg.getText());
    }

    // Method to send a regular text message to the server
    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()){
            if (!msgToSend.matches(".*\\.(png|jpe?g|gif)$")){

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 0, 10));

                Text text = new Text(msgToSend);
                text.setStyle("-fx-font-size: 14");
                TextFlow textFlow = new TextFlow(text);

//              #0693e3 #37d67a #40bf75
                //Styling for the user's own messages
                textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));

                hBox.getChildren().add(textFlow);

                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_RIGHT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));

                //Get the current time and format it
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");

                hBoxTime.getChildren().add(time);

                //Add the message and its timestamp to the vBox
                vBox.getChildren().add(hBox);
                vBox.getChildren().add(hBoxTime);


                try {
                    //Send the message to the server
                    dataOutputStream.writeUTF(clientName + "-" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Clear the text field after sending the message
                txtMsg.clear();
            }
        }
    }

    @FXML
    void txtMsgOnAction(ActionEvent actionEvent) {
        // Trigger the send button's action when the user presses Enter in the text field
        sendBtnOnAction(actionEvent);
    }

    // Static method to receive messages from server
    public static void receiveMessage(String msgFromClient, VBox vBox) {
        if (msgFromClient.matches(".*\\.(png|jpe?g|gif)$")){
            // If the message is an image
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);

            // Extract the sender's name from the message
            Text textName = new Text(msgFromClient.split("[-]")[0]);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            // Create an ImageView from the image file
            Image image = new Image(msgFromClient.split("[-]")[1]);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);

            // Create an HBox to hold the image
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));
            hBox.getChildren().add(imageView);

            // Update the UI in the JavaFX application thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBoxName);
                    vBox.getChildren().add(hBox);
                }
            });

        }else {
            // If the message is a regular text message
            String name = msgFromClient.split("-")[0];
            String msgFromServer = msgFromClient.split("-")[1];

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);

            // Set the sender's name in the UI
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            // Create a TextFlow for the message
            Text text = new Text(msgFromServer);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0,0,0));

            hBox.getChildren().add(textFlow);

            // Update the UI in the JavaFX application thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBoxName);
                    vBox.getChildren().add(hBox);
                }
            });
        }
    }

    // Method to receive and display images
    private static void receiveImage(String msgFromClient, VBox vBox) {
        /*// Extract the sender's name from the message
        String name = msgFromClient.split("[-]")[0];

        // Create an ImageView from the image file
        Image image = new Image(msgFromClient.split("[-]")[1]);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        // Create an HBox to hold the image
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.getChildren().add(imageView);

        // Update the UI in the JavaFX application thread
        Platform.runLater(() -> {
            // Set the sender's name in the UI
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            vBox.getChildren().add(hBoxName);
            vBox.getChildren().add(hBox);
        });*/
        // Extract the sender's name from the message
        String name = msgFromClient.split("[-]")[0];

        // Create an ImageView from the image file
        String imagePath = msgFromClient.split("[-]")[1];

        // Use File to create a file URL
        File file = new File(imagePath);
        String fileUrl = file.toURI().toString();

        // Create an Image using the file URL
        Image image = new Image(fileUrl);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        // Create an HBox to hold the image
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.getChildren().add(imageView);

        // Update the UI in the JavaFX application thread
        Platform.runLater(() -> {
            // Set the sender's name in the UI
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            vBox.getChildren().add(hBoxName);
            vBox.getChildren().add(hBox);
        });
    }

    // Method to set the client's name
    public void setClientName(String name) {
        clientName = name;
    }

    // Method to perform cleanup tasks when the client exists
    public void shutdown() {
        // Notify the server that the client left
        ServerFormController.receiveMessage(clientName+" left.");
    }

}
