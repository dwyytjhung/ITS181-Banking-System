package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.CardRequest;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class AdminDashboardController {

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label totalDepositsLabel;

    @FXML
    private Label totalTransactionsLabel;

    @FXML
    private Label pendingRequestsLabel;

    @FXML
    public void initialize() {
        // Calculate Total Customers
        long customerCount = AuthService.getInstance().getAllUsers().values().stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER).count();
        totalCustomersLabel.setText(String.valueOf(customerCount));

        // Calculate Total Deposits
        List<Account> accounts = BankingService.getInstance().getAllAccounts();
        double totalDeposits = accounts.stream().mapToDouble(Account::getBalance).sum();
        totalDepositsLabel.setText(String.format("$%,.2f", totalDeposits));

        // Calculate Total Transactions
        int txCount = BankingService.getInstance().getAllTransactions().size();
        totalTransactionsLabel.setText(String.valueOf(txCount));

        // Calculate Pending Card Requests
        long pendingCount = BankingService.getInstance().getAllCardRequests().stream()
                .filter(c -> c.getStatus() == CardRequest.Status.PENDING).count();
        pendingRequestsLabel.setText(String.valueOf(pendingCount));
    }
}
