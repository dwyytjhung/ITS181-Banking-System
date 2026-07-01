package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.Transaction;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatementsController {

    @FXML
    private ComboBox<String> accountCombo;

    @FXML
    private TextArea documentArea;

    @FXML
    public void initialize() {
        String customerId = AuthService.getInstance().getCurrentUser().getId();
        List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(customerId);

        accountCombo.setItems(FXCollections.observableArrayList(
                accounts.stream().map(Account::getAccountNumber).collect(Collectors.toList())
        ));

        if (!accounts.isEmpty()) {
            accountCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleGenerateStatement() {
        String accNum = accountCombo.getValue();
        if (accNum == null) return;

        Account acc = BankingService.getInstance().getAccount(accNum);
        List<Transaction> transactions = BankingService.getInstance().getTransactionsByAccount(accNum);
        transactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           PROSPERA BANKING\n");
        sb.append("           MONTHLY STATEMENT\n");
        sb.append("========================================\n\n");
        
        sb.append("Customer: ").append(AuthService.getInstance().getCurrentUser().getFullName()).append("\n");
        sb.append("Account Number: ").append(accNum).append("\n");
        sb.append("Current Balance: $").append(String.format("%,.2f", acc.getBalance())).append("\n");
        sb.append("Date Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n\n");
        
        sb.append("RECENT TRANSACTIONS:\n");
        sb.append("----------------------------------------\n");
        for (Transaction t : transactions) {
            sb.append(t.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(" | ");
            sb.append(String.format("%-12s", t.getType())).append(" | ");
            sb.append(String.format("$%,.2f", t.getAmount())).append("\n");
            sb.append("  ").append(t.getDescription()).append("\n");
        }
        sb.append("========================================\n");

        documentArea.setText(sb.toString());
    }

    @FXML
    private void handleGenerateReceipt() {
        String accNum = accountCombo.getValue();
        if (accNum == null) return;

        List<Transaction> transactions = BankingService.getInstance().getTransactionsByAccount(accNum);
        if (transactions.isEmpty()) {
            documentArea.setText("No recent transactions to generate a receipt for.");
            return;
        }

        // Get the latest transaction for receipt
        transactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        Transaction latest = transactions.get(0);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           PROSPERA BANKING\n");
        sb.append("          TRANSACTION RECEIPT\n");
        sb.append("========================================\n\n");
        
        sb.append("Receipt ID: ").append(latest.getId()).append("\n");
        sb.append("Date: ").append(latest.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Account Number: ").append(accNum).append("\n");
        sb.append("Transaction Type: ").append(latest.getType()).append("\n");
        sb.append("Amount: $").append(String.format("%,.2f", latest.getAmount())).append("\n");
        sb.append("Description: ").append(latest.getDescription()).append("\n\n");
        
        sb.append("        Thank you for choosing PROSPERA!\n");
        sb.append("========================================\n");

        documentArea.setText(sb.toString());
    }
}
