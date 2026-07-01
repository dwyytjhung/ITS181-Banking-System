package com.gabriel.twoforms.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utility class for showing standardized confirmation dialogs across the app.
 */
public class DialogUtil {

    /**
     * Shows a confirmation dialog and returns true if the user clicked Confirm.
     *
     * @param title   The window title.
     * @param header  The bold header text.
     * @param body    The detailed message body.
     * @param confirmLabel The label for the confirm button (e.g., "Yes, Transfer", "Delete Account").
     */
    public static boolean confirm(String title, String header, String body, String confirmLabel) {
        ButtonType confirmBtn = new ButtonType(confirmLabel, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn  = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, body, confirmBtn, cancelBtn);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // Apply styles
        try {
            String cssPath = DialogUtil.class.getResource("/com/gabriel/twoforms/css/styles.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(cssPath);
            alert.getDialogPane().getStyleClass().add("dialog-pane");
        } catch (Exception e) {
            // Fallback styling
            alert.getDialogPane().setStyle("-fx-font-family: 'Arial', sans-serif; -fx-font-size: 14px;");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmBtn;
    }
}
