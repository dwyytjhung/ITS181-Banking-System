package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.utils.ViewLoader;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView logoImage;

    @FXML
    private Label wordmarkLabel;

    @FXML
    private Label taglineLabel;

    @FXML
    public void initialize() {
        // Start everything invisible
        rootPane.setOpacity(0);

        // Fade in the whole splash
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Hold for a moment
        PauseTransition hold = new PauseTransition(Duration.seconds(2.0));

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, hold, fadeOut);
        sequence.setOnFinished(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            ViewLoader.loadScene(stage, "welcome.fxml", "PROSPERA - Welcome");
        });
        sequence.play();
    }
}
