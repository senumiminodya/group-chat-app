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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.groupChat.server.ServerHandler;

import java.io.IOException;

public class ServerFormController {
    @FXML
    private JFXButton addBtn;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vBox;

    @FXML
    private AnchorPane pane;
    private static VBox staticVBox; // A static reference to VBox for updating messages
    private ServerHandler serverHandler; // Instance of ServerHandler to manage server operations

    public void initialize() {
        staticVBox = vBox; // Assign the instance of vBox to the static reference
        receiveMessage("Sever Starting..");

        // Listener to automatically scroll the scrollpane when the vBox height changes
        vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });

        // Start the server in a separate thread
        new Thread(() -> {
            try {
                serverHandler = ServerHandler.getInstance(); // Get or create the singleton instance of ServerHandler
                serverHandler.makeSocket(); // Initialize the server socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        receiveMessage("Sever Running..");
        receiveMessage("Waiting for User..");
    }

    // A static method to receive and display messages from the server
    public static void receiveMessage(String msgFromClient) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(msgFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));

        hBox.getChildren().add(textFlow);

        // Update the UI on the JavaFX Application Thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                staticVBox.getChildren().add(hBox);
            }
        });
    }

    // Event handler for the "Add" button to add a new client
    @FXML
    void addBtnOnAction(ActionEvent event) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(pane.getScene().getWindow());

        // Load the login form for adding a new client
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"something went wrong. can't add client").show();
        }
        stage.setTitle("EChat");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
}
