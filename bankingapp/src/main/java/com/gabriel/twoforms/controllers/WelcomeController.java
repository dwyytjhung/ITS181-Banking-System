package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.utils.ViewLoader;
import com.gabriel.twoforms.utils.ViewModeManager;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private VBox rootPane;

    @FXML
    private RadioButton desktopRadio;

    @FXML
    private RadioButton mobileRadio;

    @FXML
    public void initialize() {
        if (desktopRadio != null && mobileRadio != null) {
            ToggleGroup group = new ToggleGroup();
            desktopRadio.setToggleGroup(group);
            mobileRadio.setToggleGroup(group);

            if (ViewModeManager.getViewMode() == ViewModeManager.ViewMode.MOBILE) {
                mobileRadio.setSelected(true);
            } else {
                desktopRadio.setSelected(true);
            }

            group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    ViewModeManager.ViewMode newMode = (newVal == mobileRadio) ? ViewModeManager.ViewMode.MOBILE : ViewModeManager.ViewMode.DESKTOP;
                    if (newMode != ViewModeManager.getViewMode()) {
                        Stage stage = (Stage) rootPane.getScene().getWindow();
                        ViewModeManager.applyViewMode(stage, newMode);
                        ViewLoader.loadScene(stage, "welcome.fxml", "PROSPERA - Welcome");
                    }
                }
            });
        }
    }

    @FXML
    private void handleCustomerLogin() {
        LoginController.setIsAdminLogin(false);
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewLoader.loadScene(stage, "login.fxml", "PROSPERA - Customer Login");
    }

    @FXML
    private void handleOpenAccount() {
        LoginController.setIsAdminLogin(false);
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewLoader.loadScene(stage, "login.fxml", "PROSPERA - Create Account");
    }

    @FXML
    private void handleAdminLogin() {
        LoginController.setIsAdminLogin(true);
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewLoader.loadScene(stage, "login.fxml", "PROSPERA - Admin Login");
    }
}
