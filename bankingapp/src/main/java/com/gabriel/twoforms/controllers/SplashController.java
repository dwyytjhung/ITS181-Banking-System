package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.utils.ViewLoader;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private VBox rootPane;

    @FXML
    public void initialize() {
        // Wait 1.5 seconds then go to Welcome
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            ViewLoader.loadScene(stage, "welcome.fxml", "PROSPERA - Welcome");
        });
        delay.play();
    }
}
