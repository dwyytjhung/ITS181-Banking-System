package com.gabriel.twoforms;

import java.lang.reflect.Method;

public class BankingLauncher {
    public static void main(String[] args) {
        try {
            // Use reflection to call Application.launch(BankingApplication.class, args)
            // This prevents the JVM from detecting JavaFX classes during class loading
            Class<?> appClass = Class.forName("javafx.application.Application");
            Method launchMethod = appClass.getMethod("launch", Class.class, String[].class);
            launchMethod.invoke(null, BankingApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
