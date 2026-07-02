package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.CardRequest;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import com.gabriel.twoforms.utils.DialogUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class AdminCardRequestsController {

    @FXML
    private TableView<CardRequest> requestTable;

    @FXML
    private TableColumn<CardRequest, String> idCol;

    @FXML
    private TableColumn<CardRequest, String> custCol;

    @FXML
    private TableColumn<CardRequest, String> typeCol;

    @FXML
    private TableColumn<CardRequest, String> statusCol;

    @FXML
    private TableColumn<CardRequest, Void> actionCol;

    @FXML
    private VBox requestsContainer;

    @FXML
    public void initialize() {
        if (requestTable != null) {
            setupTable();
        }
        refreshTable();
    }

    private void setupTable() {
        Map<String, User> allUsers = AuthService.getInstance().getAllUsers();

        idCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId().substring(0, 8)));

        custCol.setCellValueFactory(cell -> {
            String custId = cell.getValue().getCustomerId();
            String name = allUsers.values().stream()
                .filter(u -> u.getId().equals(custId))
                .map(User::getFullName)
                .findFirst()
                .orElse(custId);
            return new SimpleStringProperty(name);
        });

        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType().toString().replace("_", " ")));
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus().toString()));

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox pane = new HBox(approveBtn, rejectBtn);

            {
                approveBtn.getStyleClass().add("button-primary");
                rejectBtn.getStyleClass().add("button-secondary");
                pane.setSpacing(10);

                approveBtn.setOnAction(event -> {
                    CardRequest req = getTableView().getItems().get(getIndex());
                    String custName = allUsers.values().stream()
                        .filter(u -> u.getId().equals(req.getCustomerId()))
                        .map(User::getFullName).findFirst().orElse(req.getCustomerId());
                    String body = "Customer Name:    " + custName
                        + "\nCard Type:        " + req.getType().toString().replace("_", " ")
                        + "\n\nAn active card will be issued and the customer will be notified.";

                    boolean confirmed = DialogUtil.confirm(
                        "Confirm Card Approval",
                        "Are you sure you want to approve this card request?",
                        body,
                        "Yes, Approve Request"
                    );
                    if (!confirmed) return;
                    BankingService.getInstance().updateCardRequestStatus(req.getId(), CardRequest.Status.APPROVED);
                    refreshTable();
                });

                rejectBtn.setOnAction(event -> {
                    CardRequest req = getTableView().getItems().get(getIndex());
                    String custName = allUsers.values().stream()
                        .filter(u -> u.getId().equals(req.getCustomerId()))
                        .map(User::getFullName).findFirst().orElse(req.getCustomerId());
                    String body = "Customer Name:    " + custName
                        + "\nCard Type:        " + req.getType().toString().replace("_", " ")
                        + "\n\nThe request will be declined and the customer will be notified.";

                    boolean confirmed = DialogUtil.confirm(
                        "Confirm Card Rejection",
                        "Are you sure you want to reject this card request?",
                        body,
                        "Yes, Reject Request"
                    );
                    if (!confirmed) return;
                    BankingService.getInstance().updateCardRequestStatus(req.getId(), CardRequest.Status.REJECTED);
                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CardRequest req = getTableView().getItems().get(getIndex());
                    if (req.getStatus() == CardRequest.Status.PENDING) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void refreshTable() {
        List<CardRequest> allReqs = BankingService.getInstance().getAllCardRequests();
        
        if (requestTable != null) {
            requestTable.setItems(FXCollections.observableArrayList(allReqs));
        }

        if (requestsContainer != null) {
            requestsContainer.getChildren().clear();
            Map<String, User> allUsers = AuthService.getInstance().getAllUsers();
            for (CardRequest req : allReqs) {
                requestsContainer.getChildren().add(createMobileRequestCard(req, allUsers));
            }
            if (allReqs.isEmpty()) {
                Label emptyLabel = new Label("No card requests found.");
                emptyLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
                requestsContainer.getChildren().add(emptyLabel);
            }
        }
    }

    private VBox createMobileRequestCard(CardRequest req, Map<String, User> allUsers) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 5, 0, 0, 2);");
        
        String custId = req.getCustomerId();
        String custName = allUsers.values().stream()
            .filter(u -> u.getId().equals(custId))
            .map(User::getFullName)
            .findFirst()
            .orElse(custId);

        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        VBox titleDetails = new VBox();
        titleDetails.setSpacing(2);
        javafx.scene.layout.HBox.setHgrow(titleDetails, javafx.scene.layout.Priority.ALWAYS);
        
        Label nameLabel = new Label(custName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
        
        Label typeLabel = new Label(req.getType().toString().replace("_", " "));
        typeLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        
        titleDetails.getChildren().addAll(nameLabel, typeLabel);
        
        Label statusLabel = new Label(req.getStatus().toString());
        if (req.getStatus() == CardRequest.Status.APPROVED) {
            statusLabel.getStyleClass().add("badge-success");
        } else if (req.getStatus() == CardRequest.Status.REJECTED) {
            statusLabel.getStyleClass().add("badge-error");
        } else {
            statusLabel.getStyleClass().add("badge-pending");
        }
        
        header.getChildren().addAll(titleDetails, statusLabel);
        card.getChildren().add(header);
        
        if (req.getStatus() == CardRequest.Status.PENDING) {
            HBox actionButtons = new HBox();
            actionButtons.setSpacing(10);
            actionButtons.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            
            Button approveBtn = new Button("Approve");
            approveBtn.getStyleClass().add("button-primary");
            approveBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");
            
            Button rejectBtn = new Button("Reject");
            rejectBtn.getStyleClass().add("button-secondary");
            rejectBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");
            
            approveBtn.setOnAction(event -> {
                String body = "Customer Name:    " + custName
                    + "\nCard Type:        " + req.getType().toString().replace("_", " ")
                    + "\n\nAn active card will be issued and the customer will be notified.";

                boolean confirmed = DialogUtil.confirm(
                    "Confirm Card Approval",
                    "Are you sure you want to approve this card request?",
                    body,
                    "Yes, Approve Request"
                );
                if (!confirmed) return;
                BankingService.getInstance().updateCardRequestStatus(req.getId(), CardRequest.Status.APPROVED);
                refreshTable();
            });
            
            rejectBtn.setOnAction(event -> {
                String body = "Customer Name:    " + custName
                    + "\nCard Type:        " + req.getType().toString().replace("_", " ")
                    + "\n\nThe request will be declined and the customer will be notified.";

                boolean confirmed = DialogUtil.confirm(
                    "Confirm Card Rejection",
                    "Are you sure you want to reject this card request?",
                    body,
                    "Yes, Reject Request"
                );
                if (!confirmed) return;
                BankingService.getInstance().updateCardRequestStatus(req.getId(), CardRequest.Status.REJECTED);
                refreshTable();
            });
            
            actionButtons.getChildren().addAll(approveBtn, rejectBtn);
            card.getChildren().add(actionButtons);
        }
        
        return card;
    }
}
