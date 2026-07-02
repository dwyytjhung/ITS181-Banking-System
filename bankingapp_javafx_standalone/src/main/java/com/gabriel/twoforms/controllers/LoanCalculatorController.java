package com.gabriel.twoforms.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoanCalculatorController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField rateField;

    @FXML
    private TextField monthsField;

    @FXML
    private Label resultLabel;

    @FXML
    private void handleCalculate() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            double annualRate = Double.parseDouble(rateField.getText());
            int months = Integer.parseInt(monthsField.getText());

            double monthlyRate = (annualRate / 100) / 12;
            double monthlyPayment;

            if (monthlyRate == 0) {
                monthlyPayment = amount / months;
            } else {
                monthlyPayment = (amount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -months));
            }

            resultLabel.setText(String.format("Estimated Monthly Payment: $%,.2f", monthlyPayment));
            resultLabel.getStyleClass().setAll("badge-success");
            resultLabel.setVisible(true);
            resultLabel.setManaged(true);
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers.");
            resultLabel.getStyleClass().setAll("badge-error");
            resultLabel.setVisible(true);
            resultLabel.setManaged(true);
        }
    }
}
