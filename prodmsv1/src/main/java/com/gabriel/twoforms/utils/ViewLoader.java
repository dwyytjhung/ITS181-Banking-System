package com.gabriel.twoforms.utils;

import com.gabriel.twoforms.BankingApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewLoader {

    public static void loadView(Pane parentPane, String fxmlPath) {
        try {
            String resolvedPath = fxmlPath;
            if (ViewModeManager.getViewMode() == ViewModeManager.ViewMode.MOBILE) {
                if (!fxmlPath.contains("_mobile")) {
                    resolvedPath = fxmlPath.replace(".fxml", "_mobile.fxml");
                }
            }
            FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("views/" + resolvedPath));
            Parent view = loader.load();
            parentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            String resolvedPath = fxmlPath;
            if (ViewModeManager.getViewMode() == ViewModeManager.ViewMode.MOBILE) {
                if (!fxmlPath.contains("_mobile")) {
                    resolvedPath = fxmlPath.replace(".fxml", "_mobile.fxml");
                }
            }
            
            FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("views/" + resolvedPath));
            double width = ViewModeManager.getViewMode() == ViewModeManager.ViewMode.MOBILE ? 480 : 1440;
            double height = ViewModeManager.getViewMode() == ViewModeManager.ViewMode.MOBILE ? 850 : 900;
            
            Scene scene = new Scene(loader.load(), width, height);
            scene.getStylesheets().add(BankingApplication.class.getResource("css/styles.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            
            stage.setWidth(width);
            stage.setHeight(height);
            ViewModeManager.centerStage(stage);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
