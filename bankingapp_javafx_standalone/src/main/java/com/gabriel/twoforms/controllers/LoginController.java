package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.utils.ViewLoader;
import com.gabriel.twoforms.utils.ViewModeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    private static boolean isAdminLogin = false;

    @FXML
    private VBox rootPane;

    @FXML
    private Label loginTitle;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private RadioButton desktopRadio;

    @FXML
    private RadioButton mobileRadio;

    public static void setIsAdminLogin(boolean admin) {
        isAdminLogin = admin;
    }

    @FXML
    public void initialize() {
        if (isAdminLogin) {
            loginTitle.setText("Administrator Login");
        } else {
            loginTitle.setText("Customer Login");
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
                        Stage stage = (Stage) rootPane.getScene().getWindow();
                        ViewModeManager.applyViewMode(stage, newMode);
                        ViewLoader.loadScene(stage, "login.fxml", loginTitle.getText());
                    }
                }
            });
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        boolean success = AuthService.getInstance().login(username, password);
        if (success) {
            // Verify role matches login type
            boolean isUserAdmin = AuthService.getInstance().getCurrentUser().getRole() == com.gabriel.twoforms.models.User.Role.ADMIN;
            if (isUserAdmin != isAdminLogin) {
                errorLabel.setText("Access denied. Incorrect role.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                AuthService.getInstance().logout();
                return;
            }

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ViewLoader.loadScene(stage, "main_dashboard.fxml", "PROSPERA - Dashboard");
        } else {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewLoader.loadScene(stage, "welcome.fxml", "PROSPERA - Welcome");
    }
}
