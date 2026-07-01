package com.gabriel.twoforms.utils;

import javafx.stage.Stage;

public class ViewModeManager {
    public enum ViewMode {
        DESKTOP,
        MOBILE
    }

    private static ViewMode currentMode = ViewMode.DESKTOP;
    private static String activeViewFxml = null;

    public static ViewMode getViewMode() {
        return currentMode;
    }

    public static void setViewMode(ViewMode mode) {
        currentMode = mode;
    }

    public static String getActiveViewFxml() {
        return activeViewFxml;
    }

    public static void setActiveViewFxml(String fxml) {
        activeViewFxml = fxml;
    }

    public static void applyViewMode(Stage stage, ViewMode mode) {
        currentMode = mode;
        double width = (mode == ViewMode.MOBILE) ? 420 : 1440;
        double height = (mode == ViewMode.MOBILE) ? 850 : 900;
        
        stage.setWidth(width);
        stage.setHeight(height);
        centerStage(stage);
    }

    public static void centerStage(Stage stage) {
        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
    }
}
