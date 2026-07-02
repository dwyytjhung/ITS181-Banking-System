package com.gabriel.twoforms;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

public class DebugDashboard {
    public static void main(String[] args) {
        try {
            // Initialize JavaFX toolkit
            Platform.startup(() -> {
                try {
                    System.out.println("JavaFX toolkit initialized. Loading FXML...");
                    
                    FXMLLoader loader = new FXMLLoader(BankingApplication.class.getResource("views/admin_dashboard_mobile.fxml"));
                    loader.load();
                    
                    System.out.println("FXML loaded successfully!");
                    Platform.exit();
                } catch (Throwable t) {
                    System.out.println("ERROR LOADING FXML:");
                    t.printStackTrace();
                    Platform.exit();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
