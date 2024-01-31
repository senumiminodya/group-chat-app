package lk.ijse.groupChat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.groupChat.controller.ClientFormController;

public class ClientLauncher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ClientForm.fxml"));
        ClientFormController controller = new ClientFormController();
        fxmlLoader.setController(controller);
        primaryStage.setScene(new Scene(fxmlLoader.load()));

        //Create a new stage for the login form
        Stage stage = new Stage();
        //Set modality to WINDOW_MODEL (blocks events from being delivered to any other windows)
        stage.initModality(Modality.WINDOW_MODAL);
        //Set the owner of the login stage to the primary stage
        stage.initOwner(primaryStage.getScene().getWindow());
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))));
        stage.setTitle("EChat");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
}
