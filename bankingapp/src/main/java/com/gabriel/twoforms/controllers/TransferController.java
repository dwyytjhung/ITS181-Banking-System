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

public class TransferController {

    @FXML
    private ComboBox<Account> fromAccountCombo;

    @FXML
    private TextField toAccountField;

    @FXML
    private TextField amountField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        String customerId = AuthService.getInstance().getCurrentUser().getId();
        List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(customerId);
        
        fromAccountCombo.setItems(FXCollections.observableArrayList(accounts));
        fromAccountCombo.setConverter(new StringConverter<Account>() {
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
            fromAccountCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleTransfer() {
        Account fromAcc = fromAccountCombo.getValue();
        String toAccNum = toAccountField.getText();
        String amountStr = amountField.getText();
        String desc = descriptionField.getText();

        if (fromAcc == null || toAccNum.isEmpty() || amountStr.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }

        if (fromAcc.getAccountNumber().equals(toAccNum)) {
            showError("Cannot transfer to the same account.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }

            double expectedBalance = fromAcc.getBalance() - amount;
            String body = "From Account:     " + fromAcc.getAccountNumber()
                + "\nTo Account:       " + toAccNum
                + "\nAmount:           $" + String.format("%,.2f", amount)
                + "\nExpected Balance: $" + String.format("%,.2f", expectedBalance)
                + (desc.isEmpty() ? "" : "\nNote:             " + desc)
                + "\n\nWarning: This action is irreversible. Please verify the recipient's account number.";

            boolean confirmed = DialogUtil.confirm(
                "Confirm Transfer",
                "Are you sure you want to transfer $" + String.format("%,.2f", amount) + "?",
                body,
                "Yes, Transfer"
            );
            if (!confirmed) return;

            boolean success = BankingService.getInstance().transfer(fromAcc.getAccountNumber(), toAccNum, amount, desc);
            if (success) {
                showSuccess("Transfer successful!");
                amountField.clear();
                toAccountField.clear();
                descriptionField.clear();
                // Refresh combo box items to update balance
                fromAccountCombo.setItems(FXCollections.observableArrayList(
                    BankingService.getInstance().getAccountsByCustomer(AuthService.getInstance().getCurrentUser().getId())
                ));
                fromAccountCombo.getSelectionModel().selectFirst();
            } else {
                showError("Transfer failed. Check balance and recipient account number.");
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
