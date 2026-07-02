package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.SavingsGoal;
import com.gabriel.twoforms.models.SavingsTransaction;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SavingsGoalsController {

    // View Panels
    @FXML private VBox listPanel;
    @FXML private VBox createPanel;
    @FXML private Pane detailPanel; // Could be HBox (desktop) or VBox (mobile)

    // Goals Listing
    @FXML private FlowPane goalsContainer; // Desktop
    @FXML private VBox goalsContainerMobile; // Mobile

    // Create Goal Form
    @FXML private TextField newGoalNameField;
    @FXML private TextField newGoalTargetField;
    @FXML private TextField newGoalDescField;
    @FXML private TextField newGoalDateField;
    @FXML private Label createStatusLabel;

    // Detailed View Labels & Controls
    @FXML private Label detailNameLabel;
    @FXML private Label detailDescLabel;
    @FXML private Label detailTargetLabel;
    @FXML private Label detailSavedLabel;
    @FXML private Label detailRemainingLabel;
    @FXML private Label detailDateLabel;
    @FXML private Label detailStatusLabel;
    @FXML private Label detailBadgeLabel;
    @FXML private ProgressBar detailProgressBar;
    @FXML private TextField amountField;
    @FXML private Label actionStatusLabel;

    // Desktop History Table
    @FXML private TableView<SavingsTransaction> savingsTransactionsTable;
    @FXML private TableColumn<SavingsTransaction, String> stxDateCol;
    @FXML private TableColumn<SavingsTransaction, String> stxTypeCol;
    @FXML private TableColumn<SavingsTransaction, String> stxAmountCol;
    @FXML private TableColumn<SavingsTransaction, String> stxBalanceCol;

    // Mobile History List
    @FXML private VBox mobileTransactionsContainer;

    public static String initialGoalIdToSelect;

    private User currentUser;
    private SavingsGoal selectedGoal;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // Initialize Table Columns if they exist (desktop view)
        if (savingsTransactionsTable != null) {
            stxDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getTimestamp().format(DATE_TIME_FORMATTER)));
            stxTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getType().toString()));
            stxAmountCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    String.format("$%,.2f", cellData.getValue().getAmount())));
            stxBalanceCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    String.format("$%,.2f", cellData.getValue().getUpdatedBalance())));
        }

        loadGoals();

        if (initialGoalIdToSelect != null) {
            String goalId = initialGoalIdToSelect;
            initialGoalIdToSelect = null;
            List<SavingsGoal> goals = BankingService.getInstance().getSavingsGoalsByCustomer(currentUser.getId());
            for (SavingsGoal goal : goals) {
                if (goal.getId().equals(goalId)) {
                    handleSelectGoal(goal);
                    break;
                }
            }
        }
    }

    private void loadGoals() {
        if (currentUser == null) return;
        List<SavingsGoal> goals = BankingService.getInstance().getSavingsGoalsByCustomer(currentUser.getId());

        if (goalsContainer != null) {
            goalsContainer.getChildren().clear();
        }
        if (goalsContainerMobile != null) {
            goalsContainerMobile.getChildren().clear();
        }

        for (SavingsGoal goal : goals) {
            VBox card = createGoalCard(goal);
            if (goalsContainer != null) {
                goalsContainer.getChildren().add(card);
            }
            if (goalsContainerMobile != null) {
                // For mobile we clone or recreate a card configured for mobile style
                VBox mobileCard = createGoalCard(goal);
                goalsContainerMobile.getChildren().add(mobileCard);
            }
        }
    }

    private VBox createGoalCard(SavingsGoal goal) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setStyle("-fx-pref-width: 250px; -fx-padding: 15; -fx-cursor: hand; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Label titleLabel = new Label(goal.getName());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;");
        
        double progress = goal.getTargetAmount() > 0 ? goal.getCurrentAmount() / goal.getTargetAmount() : 0.0;
        int percentage = (int) Math.min(100, Math.round(progress * 100));

        Label amountLabel = new Label(String.format("$%,.2f / $%,.2f", goal.getCurrentAmount(), goal.getTargetAmount()));
        amountLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        Label pctLabel = new Label(percentage + "%");
        pctLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #F58220;");

        HBox amountAndPct = new HBox(10);
        amountAndPct.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        amountAndPct.getChildren().addAll(amountLabel, spacer, pctLabel);

        ProgressBar pbar = new ProgressBar(Math.min(1.0, progress));
        pbar.setMaxWidth(Double.MAX_VALUE);
        pbar.setStyle("-fx-pref-height: 8px;");

        HBox badgeBox = new HBox(8);
        badgeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label(goal.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 12;");
        switch (goal.getStatus()) {
            case ACTIVE:
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;");
                break;
            case COMPLETED:
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
                break;
            case PAUSED:
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #FEF3C7; -fx-text-fill: #92400E;");
                break;
            case CANCELLED:
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
                break;
        }
        badgeBox.getChildren().add(statusLabel);

        if (goal.getStatus() == SavingsGoal.Status.COMPLETED) {
            Label rewardLabel = new Label("🏆 Achieved");
            rewardLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 12; -fx-background-color: #FEF3C7; -fx-text-fill: #D97706;");
            badgeBox.getChildren().add(rewardLabel);
        }

        card.getChildren().addAll(titleLabel, amountAndPct, pbar, badgeBox);
        card.setOnMouseClicked(e -> handleSelectGoal(goal));
        
        return card;
    }

    public void handleSelectGoal(SavingsGoal goal) {
        selectedGoal = goal;
        
        // Populate stats
        detailNameLabel.setText(goal.getName());
        detailDescLabel.setText(goal.getDescription() != null && !goal.getDescription().isEmpty() 
                ? goal.getDescription() : "No description provided.");
        detailTargetLabel.setText(String.format("$%,.2f", goal.getTargetAmount()));
        detailSavedLabel.setText(String.format("$%,.2f", goal.getCurrentAmount()));
        
        double remaining = Math.max(0.0, goal.getTargetAmount() - goal.getCurrentAmount());
        detailRemainingLabel.setText(String.format("$%,.2f", remaining));
        detailDateLabel.setText(goal.getTargetDate() != null ? goal.getTargetDate().toString() : "No target date");
        detailStatusLabel.setText(goal.getStatus().toString());

        // Update status badge design
        detailStatusLabel.getStyleClass().clear();
        switch (goal.getStatus()) {
            case ACTIVE:
                detailStatusLabel.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                break;
            case COMPLETED:
                detailStatusLabel.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                break;
            case PAUSED:
                detailStatusLabel.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                break;
            case CANCELLED:
                detailStatusLabel.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                break;
        }

        double progress = goal.getTargetAmount() > 0 ? goal.getCurrentAmount() / goal.getTargetAmount() : 0.0;
        detailProgressBar.setProgress(Math.min(1.0, progress));

        // Unlock virtual badge rewards
        detailBadgeLabel.setText(getAchievementBadge(goal));

        // Load History
        loadTransactions(goal.getId());

        // Switch panels
        listPanel.setManaged(false);
        listPanel.setVisible(false);
        createPanel.setManaged(false);
        createPanel.setVisible(false);
        
        detailPanel.setManaged(true);
        detailPanel.setVisible(true);
    }

    private String getAchievementBadge(SavingsGoal goal) {
        if (goal.getStatus() != SavingsGoal.Status.COMPLETED) {
            return "Locked 🔒";
        }
        if (goal.getTargetAmount() >= 5000) {
            return "🏆 Goal Achiever";
        } else if (goal.getTargetAmount() >= 1000) {
            return "💰 Master Saver";
        } else {
            return "🌟 Financial Milestone";
        }
    }

    private void loadTransactions(String goalId) {
        List<SavingsTransaction> transactions = BankingService.getInstance().getSavingsTransactions(goalId);
        
        // Populate desktop table
        if (savingsTransactionsTable != null) {
            savingsTransactionsTable.setItems(FXCollections.observableArrayList(transactions));
        }

        // Populate mobile container
        if (mobileTransactionsContainer != null) {
            mobileTransactionsContainer.getChildren().clear();
            if (transactions.isEmpty()) {
                Label noHistory = new Label("No savings transactions found.");
                noHistory.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
                mobileTransactionsContainer.getChildren().add(noHistory);
            } else {
                for (SavingsTransaction tx : transactions) {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 8 0 8 0; -fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0;");
                    
                    VBox left = new VBox(2);
                    Label actionLabel = new Label(tx.getType().toString());
                    actionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1E293B;");
                    Label dateLabel = new Label(tx.getTimestamp().format(DATE_TIME_FORMATTER));
                    dateLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px;");
                    left.getChildren().addAll(actionLabel, dateLabel);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    VBox right = new VBox(2);
                    right.setAlignment(Pos.CENTER_RIGHT);
                    Label amountLabel = new Label((tx.getType() == SavingsTransaction.Type.DEPOSIT ? "+" : "-") + String.format("$%,.2f", tx.getAmount()));
                    amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + 
                            (tx.getType() == SavingsTransaction.Type.DEPOSIT ? "#22C55E;" : "#EF4444;"));
                    Label balLabel = new Label(String.format("Bal: $%,.2f", tx.getUpdatedBalance()));
                    balLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px;");
                    right.getChildren().addAll(amountLabel, balLabel);

                    row.getChildren().addAll(left, spacer, right);
                    mobileTransactionsContainer.getChildren().add(row);
                }
            }
        }
    }

    @FXML
    public void handleShowCreatePanel() {
        listPanel.setManaged(false);
        listPanel.setVisible(false);
        detailPanel.setManaged(false);
        detailPanel.setVisible(false);
        
        createPanel.setManaged(true);
        createPanel.setVisible(true);
        clearCreateForm();
    }

    @FXML
    public void handleBackToList() {
        createPanel.setManaged(false);
        createPanel.setVisible(false);
        detailPanel.setManaged(false);
        detailPanel.setVisible(false);
        
        listPanel.setManaged(true);
        listPanel.setVisible(true);
        loadGoals();
    }

    private void clearCreateForm() {
        newGoalNameField.clear();
        newGoalTargetField.clear();
        newGoalDescField.clear();
        newGoalDateField.clear();
        createStatusLabel.setVisible(false);
        createStatusLabel.setManaged(false);
    }

    @FXML
    public void handleCreateGoal() {
        String name = newGoalNameField.getText().trim();
        String targetStr = newGoalTargetField.getText().trim();
        String desc = newGoalDescField.getText().trim();
        String dateStr = newGoalDateField.getText().trim();

        if (name.isEmpty() || targetStr.isEmpty()) {
            showCreateError("Goal Name and Target Amount are required!");
            return;
        }

        double targetAmount;
        try {
            targetAmount = Double.parseDouble(targetStr);
            if (targetAmount <= 0) {
                showCreateError("Target amount must be greater than zero!");
                return;
            }
        } catch (NumberFormatException e) {
            showCreateError("Target amount must be a valid number!");
            return;
        }

        if (!dateStr.isEmpty()) {
            try {
                LocalDate.parse(dateStr);
            } catch (Exception e) {
                showCreateError("Target Date must be in YYYY-MM-DD format!");
                return;
            }
        }

        SavingsGoal result = BankingService.getInstance().createSavingsGoal(currentUser.getId(), name, targetAmount, desc, dateStr);
        if (result != null) {
            // Show Success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Goal Created 🎉");
            alert.setHeaderText("Success!");
            alert.setContentText("Your savings vault \"" + name + "\" has been created successfully.");
            alert.showAndWait();
            
            handleBackToList();
        } else {
            showCreateError("Failed to create vault. A goal with this name may already exist.");
        }
    }

    private void showCreateError(String msg) {
        createStatusLabel.setText(msg);
        createStatusLabel.setTextFill(Color.RED);
        createStatusLabel.setManaged(true);
        createStatusLabel.setVisible(true);
    }

    @FXML
    public void handleDeposit() {
        performTransfer(true);
    }

    @FXML
    public void handleWithdraw() {
        performTransfer(false);
    }

    private void performTransfer(boolean isDeposit) {
        String amtStr = amountField.getText().trim();
        if (amtStr.isEmpty()) {
            showActionStatus("Please enter an amount!", true);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                showActionStatus("Amount must be greater than zero!", true);
                return;
            }
        } catch (NumberFormatException e) {
            showActionStatus("Please enter a valid transfer amount!", true);
            return;
        }

        List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(currentUser.getId());
        if (accounts.isEmpty()) {
            showActionStatus("No bank accounts found to transfer funds!", true);
            return;
        }

        Account primaryAccount = accounts.get(0);

        if (isDeposit) {
            if (primaryAccount.getBalance() < amount) {
                showActionStatus("Insufficient balance in your primary account!", true);
                return;
            }
            boolean success = BankingService.getInstance().depositToSavingsGoal(selectedGoal.getId(), primaryAccount.getAccountNumber(), amount);
            if (success) {
                showActionStatus(String.format("Successfully added $%,.2f to your vault!", amount), false);
                amountField.clear();
                refreshSelectedGoal();
            } else {
                showActionStatus("Transfer failed. Please check your connection.", true);
            }
        } else {
            if (selectedGoal.getCurrentAmount() < amount) {
                showActionStatus("You cannot withdraw more than the vault balance!", true);
                return;
            }
            boolean success = BankingService.getInstance().withdrawFromSavingsGoal(selectedGoal.getId(), primaryAccount.getAccountNumber(), amount);
            if (success) {
                showActionStatus(String.format("Successfully withdrew $%,.2f to your account!", amount), false);
                amountField.clear();
                refreshSelectedGoal();
            } else {
                showActionStatus("Withdrawal failed. Please check your connection.", true);
            }
        }
    }

    private void refreshSelectedGoal() {
        SavingsGoal updated = BankingService.getInstance().getSavingsGoal(selectedGoal.getId());
        if (updated != null) {
            // Check if goal was just completed in this transaction
            boolean justCompleted = (updated.getStatus() == SavingsGoal.Status.COMPLETED && selectedGoal.getStatus() != SavingsGoal.Status.COMPLETED);
            
            selectedGoal = updated;
            
            // Redraw labels
            detailSavedLabel.setText(String.format("$%,.2f", selectedGoal.getCurrentAmount()));
            double remaining = Math.max(0.0, selectedGoal.getTargetAmount() - selectedGoal.getCurrentAmount());
            detailRemainingLabel.setText(String.format("$%,.2f", remaining));
            detailStatusLabel.setText(selectedGoal.getStatus().toString());
            
            double progress = selectedGoal.getTargetAmount() > 0 ? selectedGoal.getCurrentAmount() / selectedGoal.getTargetAmount() : 0.0;
            detailProgressBar.setProgress(Math.min(1.0, progress));
            detailBadgeLabel.setText(getAchievementBadge(selectedGoal));
            
            loadTransactions(selectedGoal.getId());

            if (justCompleted) {
                // Play completion celebration
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Goal Completed! 🎉");
                alert.setHeaderText("Congratulations!");
                alert.setContentText("You achieved your \"" + selectedGoal.getName() + "\" savings goal!\nUnlocked Badge: " + getAchievementBadge(selectedGoal));
                alert.showAndWait();
            }
        }
    }

    private void showActionStatus(String msg, boolean isError) {
        actionStatusLabel.setText(msg);
        actionStatusLabel.setTextFill(isError ? Color.RED : Color.web("#22C55E"));
        actionStatusLabel.setManaged(true);
        actionStatusLabel.setVisible(true);
    }

    // Quick add handlers
    @FXML public void handleQuickAdd100() { quickDeposit(100.0); }
    @FXML public void handleQuickAdd500() { quickDeposit(500.0); }
    @FXML public void handleQuickAdd1000() { quickDeposit(1000.0); }
    @FXML public void handleQuickAdd5000() { quickDeposit(5000.0); }

    private void quickDeposit(double amount) {
        List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(currentUser.getId());
        if (accounts.isEmpty()) {
            showActionStatus("No bank accounts found!", true);
            return;
        }
        Account primaryAccount = accounts.get(0);
        if (primaryAccount.getBalance() < amount) {
            showActionStatus("Insufficient balance in your primary account!", true);
            return;
        }

        boolean success = BankingService.getInstance().depositToSavingsGoal(selectedGoal.getId(), primaryAccount.getAccountNumber(), amount);
        if (success) {
            showActionStatus(String.format("Quick Transferred +$%,.0f to your vault!", amount), false);
            refreshSelectedGoal();
        } else {
            showActionStatus("Quick transfer failed.", true);
        }
    }
}
