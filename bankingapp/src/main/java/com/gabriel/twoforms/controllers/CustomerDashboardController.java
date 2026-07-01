package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.Transaction;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomerDashboardController {

    @FXML
    private Label totalBalanceLabel;

    @FXML
    private Label accountsCountLabel;

    @FXML
    private ProgressBar savingsProgressBar;

    @FXML
    private VBox recentTransactionsContainer;

    @FXML
    public void initialize() {
        User currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(currentUser.getId());
            double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
            
            totalBalanceLabel.setText(String.format("$%,.2f", totalBalance));
            accountsCountLabel.setText(accounts.size() + " Active Account(s)");
            
            // Dummy savings goal progress
            savingsProgressBar.setProgress(0.65);

            if (recentTransactionsContainer != null) {
                List<Transaction> userTransactions = new ArrayList<>();
                for (Account acc : accounts) {
                    userTransactions.addAll(BankingService.getInstance().getTransactionsByAccount(acc.getAccountNumber()));
                }
                userTransactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
                
                recentTransactionsContainer.getChildren().clear();
                int count = 0;
                for (Transaction t : userTransactions) {
                    if (count >= 3) break;
                    recentTransactionsContainer.getChildren().add(createTransactionCard(t));
                    count++;
                }
                if (userTransactions.isEmpty()) {
                    Label emptyLabel = new Label("No recent transactions.");
                    emptyLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
                    recentTransactionsContainer.getChildren().add(emptyLabel);
                }
            }
        }
    }

    private HBox createTransactionCard(Transaction t) {
        HBox row = new HBox();
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setSpacing(15);
        row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 12;");
        
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(javafx.geometry.Pos.CENTER);
        iconContainer.setPrefSize(40, 40);
        iconContainer.setMinSize(40, 40);
        
        Label iconLabel = new Label();
        String color;
        if (t.getType() == Transaction.Type.DEPOSIT || t.getType() == Transaction.Type.TRANSFER_IN) {
            iconLabel.setText("↓");
            color = "#22C55E";
        } else {
            iconLabel.setText("↑");
            color = "#EF4444";
        }
        iconLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        iconContainer.setStyle("-fx-background-color: " + color + "15; -fx-background-radius: 20;");
        iconContainer.getChildren().add(iconLabel);
        
        VBox details = new VBox();
        details.setSpacing(2);
        javafx.scene.layout.HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        
        Label typeLabel = new Label(t.getType().toString().replace("_", " "));
        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
        
        Label descLabel = new Label(t.getDescription());
        descLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        descLabel.setWrapText(false);
        
        details.getChildren().addAll(typeLabel, descLabel);
        
        VBox amountBox = new VBox();
        amountBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        amountBox.setSpacing(2);
        
        String sign = (t.getType() == Transaction.Type.DEPOSIT || t.getType() == Transaction.Type.TRANSFER_IN) ? "+" : "-";
        Label amountLabel = new Label(sign + String.format("$%,.2f", t.getAmount()));
        amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + ((sign.equals("+")) ? "#22C55E" : "#1E293B") + ";");
        
        Label timeLabel = new Label(t.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd")));
        timeLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        
        amountBox.getChildren().addAll(amountLabel, timeLabel);
        
        row.getChildren().addAll(iconContainer, details, amountBox);
        return row;
    }

    @FXML
    private void handleQuickDeposit() {
        MainDashboardController.getActiveInstance().navigateToView("deposit_withdraw.fxml");
    }

    @FXML
    private void handleQuickWithdraw() {
        MainDashboardController.getActiveInstance().navigateToView("deposit_withdraw.fxml");
    }

    @FXML
    private void handleQuickTransfer() {
        MainDashboardController.getActiveInstance().navigateToView("transfer.fxml");
    }
}
