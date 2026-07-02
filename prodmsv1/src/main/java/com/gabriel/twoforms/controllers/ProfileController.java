package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.utils.ViewLoader;
import com.gabriel.twoforms.utils.ViewModeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private VBox rootPane;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private RadioButton desktopRadio;

    @FXML
    private RadioButton mobileRadio;

    @FXML
    public void initialize() {
        User user = AuthService.getInstance().getCurrentUser();
        if (user != null) {
            fullNameLabel.setText(user.getFullName());
            usernameLabel.setText("@" + user.getUsername());
            roleLabel.setText(user.getRole().toString());
        }

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
                        toggleViewMode(newMode);
                    }
                }
            });
        }
    }

    private void toggleViewMode(ViewModeManager.ViewMode mode) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewModeManager.applyViewMode(stage, mode);
        // Save profile.fxml as active view to restore it upon shell reloading
        ViewModeManager.setActiveViewFxml("profile.fxml");
        
        // Reload dashboard shell
        ViewLoader.loadScene(stage, "main_dashboard.fxml", "PROSPERA - Dashboard");
    }

    @FXML
    private void handleLogout() {
        AuthService.getInstance().logout();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewLoader.loadScene(stage, "login.fxml", "PROSPERA - Log In");
    }
}
