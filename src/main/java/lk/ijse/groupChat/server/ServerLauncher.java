package lk.ijse.groupChat.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServerLauncher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ServerForm.fxml"))));
        primaryStage.setTitle("Server");
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();

        // Create a secondary stage for login
        Stage stage = new Stage();
        // Set the modality of the secondary stage to WINDOW_MODEL
        stage.initModality(Modality.WINDOW_MODAL);
        // Set the owner of the secondary stage to the primary stage's scene window
        stage.initOwner(primaryStage.getScene().getWindow());
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))));
        stage.setTitle("EChat");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
}
