package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import com.gabriel.twoforms.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class DepositController {

    @FXML
    private ComboBox<Account> accountCombo;

    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        String customerId = AuthService.getInstance().getCurrentUser().getId();
        List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(customerId);

        accountCombo.setItems(FXCollections.observableArrayList(accounts));
        accountCombo.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account account) {
                if (account == null) return "";
                return account.getAccountNumber() + " (Bal: $" + String.format("%,.2f", account.getBalance()) + ")";
            }

            @Override
            public Account fromString(String s) {
                return null;
            }
        });

        if (!accounts.isEmpty()) {
            accountCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleDeposit() {
        Account acc = accountCombo.getValue();
        String amountStr = amountField.getText();

        if (acc == null || amountStr.isEmpty()) {
            showError("Please select an account and enter an amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }

            double expectedBalance = acc.getBalance() + amount;

            String body = "Account Number:   " + acc.getAccountNumber()
                + "\nCurrent Balance:  $" + String.format("%,.2f", acc.getBalance())
                + "\nTransaction Amt:  $" + String.format("%,.2f", amount)
                + "\nExpected Balance: $" + String.format("%,.2f", expectedBalance)
                + "\n\nPlease review the details before continuing.";

            boolean confirmed = DialogUtil.confirm(
                "Confirm Deposit",
                "Are you sure you want to deposit $" + String.format("%,.2f", amount) + "?",
                body,
                "Yes, Deposit"
            );
            if (!confirmed) return;

            boolean success = BankingService.getInstance().deposit(acc.getAccountNumber(), amount, "Deposit");

            if (success) {
                showSuccess("Deposit successful!");
                amountField.clear();
                // Refresh combo box to show new balance
                accountCombo.setItems(FXCollections.observableArrayList(
                        BankingService.getInstance().getAccountsByCustomer(AuthService.getInstance().getCurrentUser().getId())
                ));
                accountCombo.getSelectionModel().selectFirst();
            } else {
                showError("Deposit failed.");
            }
        } catch (NumberFormatException e) {
            showError("Invalid amount format.");
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().setAll("badge-error");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showSuccess(String msg) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().setAll("badge-success");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
