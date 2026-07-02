package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import com.gabriel.twoforms.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.UUID;

public class ManageAccountsController {

    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, String> accNumCol;

    @FXML
    private TableColumn<Account, String> custIdCol;

    @FXML
    private TableColumn<Account, Double> balanceCol;

    // Create Account fields
    @FXML
    private TextField newUserIdField;
    @FXML
    private TextField newUserPassField;
    @FXML
    private TextField newUserNameField;
    @FXML
    private TextField initialDepositField;
    @FXML
    private Label createStatusLabel;

    // Modify/Delete fields
    @FXML
    private TextField updateBalanceField;
    @FXML
    private Label actionStatusLabel;

    @FXML
    private VBox accountsContainer;

    private Account mobileSelectedAccount;

    @FXML
    public void initialize() {
        if (accountsTable != null) {
            accNumCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
            custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));

            accountsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    updateBalanceField.setText(String.valueOf(newSelection.getBalance()));
                }
            });
        }
        
        refreshTable();
    }

    private void refreshTable() {
        if (accountsTable != null) {
            accountsTable.setItems(FXCollections.observableArrayList(BankingService.getInstance().getAllAccounts()));
        }
        
        if (accountsContainer != null) {
            accountsContainer.getChildren().clear();
            List<Account> allAccs = BankingService.getInstance().getAllAccounts();
            for (Account acc : allAccs) {
                accountsContainer.getChildren().add(createMobileAccountCard(acc));
            }
            if (allAccs.isEmpty()) {
                Label emptyLabel = new Label("No accounts found.");
                emptyLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
                accountsContainer.getChildren().add(emptyLabel);
            }
        }
    }

    private HBox createMobileAccountCard(Account acc) {
        HBox card = new HBox();
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setSpacing(15);
        
        boolean isSelected = (mobileSelectedAccount != null && mobileSelectedAccount.getAccountNumber().equals(acc.getAccountNumber()));
        String bgStyle = isSelected ? "-fx-background-color: #FFF7ED; -fx-border-color: #F58220; -fx-border-width: 1; -fx-border-radius: 12;" : "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 12;";
        card.setStyle(bgStyle + " -fx-padding: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 5, 0, 0, 2);");
        
        VBox details = new VBox();
        details.setSpacing(4);
        javafx.scene.layout.HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        
        Label accNumLabel = new Label("Acc: " + acc.getAccountNumber());
        accNumLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
        
        Label custLabel = new Label("Cust: " + acc.getCustomerId());
        custLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        
        details.getChildren().addAll(accNumLabel, custLabel);
        
        Label balanceLabel = new Label(String.format("$%,.2f", acc.getBalance()));
        balanceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #F58220;");
        
        card.getChildren().addAll(details, balanceLabel);
        card.setCursor(javafx.scene.Cursor.HAND);
        
        card.setOnMouseClicked(e -> {
            mobileSelectedAccount = acc;
            if (updateBalanceField != null) {
                updateBalanceField.setText(String.valueOf(acc.getBalance()));
            }
            refreshTable();
        });
        
        return card;
    }

    private Account getSelectedAccount() {
        if (accountsTable != null) {
            return accountsTable.getSelectionModel().getSelectedItem();
        }
        return mobileSelectedAccount;
    }

    @FXML
    private void handleCreateAccount() {
        try {
            String username = newUserIdField.getText();
            String pass = newUserPassField.getText();
            String name = newUserNameField.getText();
            String depStr = initialDepositField.getText();

            if (username.isEmpty() || pass.isEmpty() || name.isEmpty() || depStr.isEmpty()) {
                showCreateError("All fields are required.");
                return;
            }

            double deposit = Double.parseDouble(depStr);

            String body = "Customer Name:    " + name + "\n" +
                "Username:         " + username + "\n" +
                "Initial Deposit:  $" + String.format("%,.2f", deposit) + "\n\n" +
                "A new user profile and banking account will be created.";

            boolean confirmed = DialogUtil.confirm(
                "Confirm Account Creation",
                "Are you sure you want to create this customer account?",
                body,
                "Yes, Create Account"
            );
            if (!confirmed) return;

            // Create User if not exists
            if (!AuthService.getInstance().getAllUsers().containsKey(username)) {
                User user = new User("U" + System.currentTimeMillis(), username, pass, User.Role.CUSTOMER, name);
                AuthService.getInstance().createUser(user);
            }

            // Create Account
            String newAccNum = String.valueOf(100000000 + (long)(Math.random() * 900000000));
            Account acc = BankingService.getInstance().createAccount(AuthService.getInstance().getAllUsers().get(username).getId(), newAccNum);
            
            if (acc != null) {
                BankingService.getInstance().deposit(newAccNum, deposit, "Initial Deposit");

                // Show credential popup
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Account Created Successfully");
                alert.setHeaderText("New Customer Account Details");

                String content =
                    "A new account has been created.\n\n" +
                    "Full Name:       " + name + "\n" +
                    "Username:        " + username + "\n" +
                    "Password:        " + pass + "\n" +
                    "Account Number:  " + newAccNum + "\n" +
                    "Initial Deposit: $" + String.format("%,.2f", deposit) + "\n\n" +
                    "Please share these credentials with the customer.";

                alert.setContentText(content);
                alert.getDialogPane().setStyle(
                    "-fx-font-family: 'Arial', sans-serif; -fx-font-size: 14px;"
                );

                alert.showAndWait();

                newUserIdField.clear();
                newUserPassField.clear();
                newUserNameField.clear();
                initialDepositField.clear();
                refreshTable();
            } else {
                showCreateError("Failed to create account. Username may already exist.");
            }
        } catch (NumberFormatException e) {
            showCreateError("Invalid deposit amount.");
        }
    }

    @FXML
    private void handleModifyAccount() {
        Account selected = getSelectedAccount();
        if (selected == null) {
            showActionError("Select an account first.");
            return;
        }

        try {
            double newBalance = Double.parseDouble(updateBalanceField.getText());
            if (newBalance < 0) {
                showActionError("Balance cannot be negative.");
                return;
            }

            double diff = newBalance - selected.getBalance();
            String diffStr = diff >= 0 ? "+$" + String.format("%,.2f", diff) : "-$" + String.format("%,.2f", Math.abs(diff));
            String body = "Account Number:   " + selected.getAccountNumber()
                + "\nCurrent Balance:  $" + String.format("%,.2f", selected.getBalance())
                + "\nNew Balance:      $" + String.format("%,.2f", newBalance)
                + "\nDifference:       " + diffStr
                + "\n\nWarning: This will overwrite the balance of the selected account.";

            boolean confirmed = DialogUtil.confirm(
                "Confirm Balance Update",
                "Are you sure you want to update this account's balance?",
                body,
                "Yes, Update Balance"
            );
            if (!confirmed) return;

            BankingService.getInstance().setAccountBalance(selected.getAccountNumber(), newBalance);
            refreshTable();
            showActionSuccess("Account updated.");
        } catch (NumberFormatException e) {
            showActionError("Invalid balance format.");
        }
    }

    @FXML
    private void handleDeleteAccount() {
        Account selected = getSelectedAccount();
        if (selected == null) {
            showActionError("Select an account first.");
            return;
        }

        String body = "Account Number:   " + selected.getAccountNumber()
            + "\nCurrent Balance:  $" + String.format("%,.2f", selected.getBalance())
            + "\n\nWarning: This action is IRREVERSIBLE. All transaction history and data associated with this account will be permanently deleted.";

        boolean confirmed = DialogUtil.confirm(
            "Confirm Account Deletion",
            "Are you sure you want to permanently delete this account?",
            body,
            "Yes, Delete Account"
        );
        if (!confirmed) return;

        BankingService.getInstance().deleteAccount(selected.getAccountNumber());
        
        if (mobileSelectedAccount != null && mobileSelectedAccount.getAccountNumber().equals(selected.getAccountNumber())) {
            mobileSelectedAccount = null;
        }

        refreshTable();
        updateBalanceField.clear();
        showActionSuccess("Account deleted.");
    }

    private void showCreateError(String msg) {
        createStatusLabel.setText(msg);
        createStatusLabel.getStyleClass().setAll("badge-error");
        createStatusLabel.setVisible(true);
        createStatusLabel.setManaged(true);
    }

    private void showCreateSuccess(String msg) {
        createStatusLabel.setText(msg);
        createStatusLabel.getStyleClass().setAll("badge-success");
        createStatusLabel.setVisible(true);
        createStatusLabel.setManaged(true);
    }

    private void showActionError(String msg) {
        actionStatusLabel.setText(msg);
        actionStatusLabel.getStyleClass().setAll("badge-error");
        actionStatusLabel.setVisible(true);
        actionStatusLabel.setManaged(true);
    }

    private void showActionSuccess(String msg) {
        actionStatusLabel.setText(msg);
        actionStatusLabel.getStyleClass().setAll("badge-success");
        actionStatusLabel.setVisible(true);
        actionStatusLabel.setManaged(true);
    }
}
