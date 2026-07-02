package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.SavingsGoal;
import com.gabriel.twoforms.models.Transaction;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    private VBox savingsGoalsWidgetContainer; // Desktop

    @FXML
    private VBox savingsGoalsWidgetContainerMobile; // Mobile

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
            
            // Populate Savings Goals Widget
            populateSavingsGoalsWidget(currentUser.getId());

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

    private void populateSavingsGoalsWidget(String customerId) {
        List<SavingsGoal> goals = BankingService.getInstance().getSavingsGoalsByCustomer(customerId);

        if (savingsGoalsWidgetContainer != null) {
            savingsGoalsWidgetContainer.getChildren().clear();
            renderGoalsList(goals, savingsGoalsWidgetContainer);
        }

        if (savingsGoalsWidgetContainerMobile != null) {
            savingsGoalsWidgetContainerMobile.getChildren().clear();
            renderGoalsList(goals, savingsGoalsWidgetContainerMobile);
        }
    }

    private void renderGoalsList(List<SavingsGoal> goals, VBox container) {
        if (goals.isEmpty()) {
            Label label = new Label("No savings vaults active.\nClick Savings Vault in the menu to create one!");
            label.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px; -fx-padding: 5 0 5 0;");
            container.getChildren().add(label);
            return;
        }

        int limit = 3;
        int count = 0;
        for (SavingsGoal goal : goals) {
            if (count >= limit) break;

            VBox row = new VBox(5);
            row.setStyle("-fx-padding: 8; -fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
            
            double progress = goal.getTargetAmount() > 0 ? goal.getCurrentAmount() / goal.getTargetAmount() : 0.0;
            int percentage = (int) Math.min(100, Math.round(progress * 100));

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(goal.getName() + (goal.getStatus() == SavingsGoal.Status.COMPLETED ? " ✔" : ""));
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1E293B;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label pctLabel = new Label(percentage + "%");
            pctLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #F58220;");
            header.getChildren().addAll(nameLabel, spacer, pctLabel);

            ProgressBar pbar = new ProgressBar(Math.min(1.0, progress));
            pbar.setMaxWidth(Double.MAX_VALUE);
            pbar.setStyle("-fx-pref-height: 8px;");

            row.getChildren().addAll(header, pbar);
            row.setOnMouseClicked(event -> {
                SavingsGoalsController.initialGoalIdToSelect = goal.getId();
                MainDashboardController.getActiveInstance().navigateToView("savings_goals.fxml");
            });

            container.getChildren().add(row);
            count++;
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
        MainDashboardController.getActiveInstance().navigateToView("deposit.fxml");
    }

    @FXML
    private void handleQuickWithdraw() {
        MainDashboardController.getActiveInstance().navigateToView("withdraw.fxml");
    }

    @FXML
    private void handleQuickTransfer() {
        MainDashboardController.getActiveInstance().navigateToView("transfer.fxml");
    }

    @FXML
    private void handleQuickVault() {
        MainDashboardController.getActiveInstance().navigateToView("savings_goals.fxml");
    }

    @FXML
    private void handleQuickInbox() {
        MainDashboardController.getActiveInstance().navigateToView("customer_inbox.fxml");
    }
}
