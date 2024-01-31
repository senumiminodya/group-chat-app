package lk.ijse.groupChat.controller;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {
    @FXML
    private JFXButton joinChatBtn;

    @FXML
    private TextField txtName;

    public void initialize() {

    }

    @FXML
    void joinChatBtnOnAction(ActionEvent event) throws IOException {
        // Check if the name is not empty and contains only letters and numbers
        if (!txtName.getText().isEmpty()&&txtName.getText().matches("[A-Za-z0-9]+")){
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ClientForm.fxml"));

            ClientFormController controller = new ClientFormController();
            controller.setClientName(txtName.getText()); // Set the parameter
            fxmlLoader.setController(controller);

            primaryStage.setScene(new Scene(fxmlLoader.load()));
            primaryStage.setTitle(txtName.getText());
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            // Set an event handler for the close request of the primary stage
            primaryStage.setOnCloseRequest(windowEvent -> {
                // Perform cleanup tasks when the client exists
                controller.shutdown();
            });
            primaryStage.show();

            txtName.clear();
        }else{
            new Alert(Alert.AlertType.ERROR, "Please enter your name").show();
        }
    }

}
