package com.gabriel.twoforms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BankingApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("views/splash.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 900);
        scene.getStylesheets().add(BankingApplication.class.getResource("css/styles.css").toExternalForm());
        stage.setTitle("PROSPERA");
        stage.setScene(scene);
        stage.show();
    }
}
