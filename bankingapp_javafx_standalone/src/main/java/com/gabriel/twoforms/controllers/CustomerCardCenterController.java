package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.CardRequest;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import com.gabriel.twoforms.utils.DialogUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerCardCenterController {

    @FXML
    private ComboBox<String> requestTypeCombo;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<CardRequest> requestTable;

    @FXML
    private TableColumn<CardRequest, String> dateCol;

    @FXML
    private TableColumn<CardRequest, String> typeCol;

    @FXML
    private TableColumn<CardRequest, String> statusCol;

    @FXML
    private VBox requestsContainer;

    @FXML
    public void initialize() {
        requestTypeCombo.setItems(FXCollections.observableArrayList(
            "New Card", "Replace Lost Card", "Replace Damaged Card"
        ));
        requestTypeCombo.getSelectionModel().selectFirst();

        setupTable();
        refreshTable();
    }

    private void setupTable() {
        if (requestTable != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            dateCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getRequestDate().format(formatter)));
                
            typeCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getType().toString().replace("_", " ")));
                
            statusCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        }
    }

    private void refreshTable() {
        String customerId = AuthService.getInstance().getCurrentUser().getId();
        List<CardRequest> requests = BankingService.getInstance().getCardRequestsByCustomer(customerId);
        
        if (requestTable != null) {
            requestTable.setItems(FXCollections.observableArrayList(requests));
        }

        if (requestsContainer != null) {
            requestsContainer.getChildren().clear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (CardRequest req : requests) {
                requestsContainer.getChildren().add(createCardRequestCard(req, formatter));
            }
            if (requests.isEmpty()) {
                Label emptyLabel = new Label("No card requests found.");
                emptyLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
                requestsContainer.getChildren().add(emptyLabel);
            }
        }
    }

    private HBox createCardRequestCard(CardRequest req, DateTimeFormatter formatter) {
        HBox row = new HBox();
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setSpacing(15);
        row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 5, 0, 0, 2);");
        
        VBox details = new VBox();
        details.setSpacing(4);
        javafx.scene.layout.HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        
        Label typeLabel = new Label(req.getType().toString().replace("_", " "));
        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
        
        Label dateLabel = new Label("Requested: " + req.getRequestDate().format(formatter));
        dateLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        
        details.getChildren().addAll(typeLabel, dateLabel);
        
        Label statusLbl = new Label(req.getStatus().toString());
        if (req.getStatus() == CardRequest.Status.APPROVED) {
            statusLbl.getStyleClass().add("badge-success");
        } else if (req.getStatus() == CardRequest.Status.REJECTED) {
            statusLbl.getStyleClass().add("badge-error");
        } else {
            statusLbl.getStyleClass().add("badge-pending");
        }
        
        row.getChildren().addAll(details, statusLbl);
        return row;
    }

    @FXML
    private void handleSubmitRequest() {
        String selected = requestTypeCombo.getValue();
        if (selected == null) return;

        CardRequest.Type type;
        if (selected.equals("Replace Lost Card")) type = CardRequest.Type.LOST_CARD;
        else if (selected.equals("Replace Damaged Card")) type = CardRequest.Type.DAMAGED_CARD;
        else type = CardRequest.Type.NEW_CARD;

        boolean confirmed = DialogUtil.confirm(
            "Confirm Card Request",
            "Are you sure you want to request a card?",
            "Request Type: " + selected + "\n\nThis card request will be sent to the administrator for review.",
            "Submit Request"
        );
        if (!confirmed) return;

        String customerId = AuthService.getInstance().getCurrentUser().getId();
        BankingService.getInstance().submitCardRequest(customerId, type);

        statusLabel.setText("Request submitted successfully!");
        statusLabel.getStyleClass().setAll("badge-success");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        refreshTable();
    }
}
