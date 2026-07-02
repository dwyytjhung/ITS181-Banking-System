package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.Transaction;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransactionsController {

    @FXML
    private TableView<Transaction> transactionsTable;

    @FXML
    private TableColumn<Transaction, String> dateCol;

    @FXML
    private TableColumn<Transaction, String> accountCol;

    @FXML
    private TableColumn<Transaction, String> typeCol;

    @FXML
    private TableColumn<Transaction, String> amountCol;

    @FXML
    private TableColumn<Transaction, String> descCol;

    @FXML
    private VBox transactionsContainer;

    @FXML
    public void initialize() {
        String customerId = AuthService.getInstance().getCurrentUser().getId();
        List<Account> accounts = BankingService.getInstance().getAccountsByCustomer(customerId);
        
        List<Transaction> userTransactions = new ArrayList<>();
        for (Account acc : accounts) {
            userTransactions.addAll(BankingService.getInstance().getTransactionsByAccount(acc.getAccountNumber()));
        }
        
        userTransactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (transactionsTable != null) {
            dateCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getTimestamp().format(formatter)));
                
            accountCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
            
            typeCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getType().toString()));
                
            amountCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("$%,.2f", cellData.getValue().getAmount())));
                
            descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

            transactionsTable.setItems(FXCollections.observableArrayList(userTransactions));
        }

        if (transactionsContainer != null) {
            transactionsContainer.getChildren().clear();
            for (Transaction t : userTransactions) {
                transactionsContainer.getChildren().add(createTransactionCard(t));
            }
            if (userTransactions.isEmpty()) {
                Label emptyLabel = new Label("No transactions found.");
                emptyLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
                transactionsContainer.getChildren().add(emptyLabel);
            }
        }
    }

    private HBox createTransactionCard(Transaction t) {
        HBox row = new HBox();
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setSpacing(15);
        row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 5, 0, 0, 2);");
        
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
        
        Label descLabel = new Label("Acc: " + t.getAccountId() + " | " + t.getDescription());
        descLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        descLabel.setWrapText(false);
        
        details.getChildren().addAll(typeLabel, descLabel);
        
        VBox amountBox = new VBox();
        amountBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        amountBox.setSpacing(2);
        
        String sign = (t.getType() == Transaction.Type.DEPOSIT || t.getType() == Transaction.Type.TRANSFER_IN) ? "+" : "-";
        Label amountLabel = new Label(sign + String.format("$%,.2f", t.getAmount()));
        amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + ((sign.equals("+")) ? "#22C55E" : "#1E293B") + ";");
        
        Label timeLabel = new Label(t.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        timeLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        
        amountBox.getChildren().addAll(amountLabel, timeLabel);
        
        row.getChildren().addAll(iconContainer, details, amountBox);
        return row;
    }
}
