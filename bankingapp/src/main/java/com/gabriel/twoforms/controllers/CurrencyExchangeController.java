package com.gabriel.twoforms.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class CurrencyExchangeController {

    @FXML
    private ComboBox<String> fromCurrencyCombo;

    @FXML
    private ComboBox<String> toCurrencyCombo;

    @FXML
    private TextField amountField;

    @FXML
    private Label resultLabel;

    private final Map<String, Double> ratesToUSD = new HashMap<>();

    @FXML
    public void initialize() {
        // Mock rates relative to USD
        ratesToUSD.put("USD", 1.0);
        ratesToUSD.put("PHP", 58.5);
        ratesToUSD.put("EUR", 0.92);
        ratesToUSD.put("JPY", 155.0);

        fromCurrencyCombo.setItems(FXCollections.observableArrayList(ratesToUSD.keySet()));
        toCurrencyCombo.setItems(FXCollections.observableArrayList(ratesToUSD.keySet()));

        fromCurrencyCombo.getSelectionModel().select("USD");
        toCurrencyCombo.getSelectionModel().select("PHP");
    }

    @FXML
    private void handleConvert() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = fromCurrencyCombo.getValue();
            String to = toCurrencyCombo.getValue();

            double amountInUSD = amount / ratesToUSD.get(from);
            double result = amountInUSD * ratesToUSD.get(to);

            resultLabel.setText(String.format("%,.2f %s = %,.2f %s", amount, from, result, to));
            resultLabel.getStyleClass().setAll("badge-success");
            resultLabel.setVisible(true);
            resultLabel.setManaged(true);
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid amount.");
            resultLabel.getStyleClass().setAll("badge-error");
            resultLabel.setVisible(true);
            resultLabel.setManaged(true);
        }
    }
}
