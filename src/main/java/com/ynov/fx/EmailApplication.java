package com.ynov.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EmailApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EmailApplication.class.getResource("email-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

            EmailController controller = fxmlLoader.getController();
            controller.updateStatusText("Email Inbox Desktop Application");

            primaryStage.setTitle("Email Inbox");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}