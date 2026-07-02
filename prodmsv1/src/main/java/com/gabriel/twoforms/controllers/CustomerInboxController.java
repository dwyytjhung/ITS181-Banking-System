package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.Notification;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.services.BankingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerInboxController {

    @FXML
    private Label unreadBadge;

    @FXML
    private TableView<Notification> inboxTable;

    @FXML
    private TableColumn<Notification, String> dateCol;

    @FXML
    private TableColumn<Notification, String> titleCol;

    @FXML
    private TableColumn<Notification, String> readCol;

    @FXML
    private VBox messagePanel;

    @FXML
    private VBox placeholderPanel;

    @FXML
    private Label messageTitleLabel;

    @FXML
    private Label messageDateLabel;

    @FXML
    private TextArea messageBodyArea;

    @FXML
    private VBox inboxContainer;

    @FXML
    private VBox notificationListPane;

    @FXML
    private Button backBtn;

    private String userId;
    // Guard flag to prevent recursive openMessage calls caused by setItems() clearing selection
    private boolean updatingInbox = false;

    @FXML
    public void initialize() {
        userId = AuthService.getInstance().getCurrentUser().getId();
        setupTable();
        
        if (inboxTable != null) {
            inboxTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
                if (selected != null && !updatingInbox) {
                    openMessage(selected);
                }
            });
        }

        if (backBtn != null) {
            backBtn.setOnAction(e -> {
                if (messagePanel != null) {
                    messagePanel.setVisible(false);
                    messagePanel.setManaged(false);
                }
                if (notificationListPane != null) {
                    notificationListPane.setVisible(true);
                    notificationListPane.setManaged(true);
                }
                if (placeholderPanel != null) {
                    placeholderPanel.setVisible(true);
                    placeholderPanel.setManaged(true);
                }
            });
        }

        refreshInbox();
    }

    private void setupTable() {
        if (inboxTable != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm");

            dateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTimestamp().format(fmt)));

            titleCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));

            readCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().isRead() ? "Read" : "● New"));

            inboxTable.setRowFactory(tv -> new javafx.scene.control.TableRow<Notification>() {
                @Override
                protected void updateItem(Notification item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (!item.isRead()) {
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-font-weight: normal;");
                    }
                }
            });
        }
    }

    private void refreshInbox() {
        List<Notification> notifs = BankingService.getInstance().getNotificationsByUser(userId);
        notifs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        // Suppress selection listener while we swap out table items
        updatingInbox = true;
        if (inboxTable != null) {
            inboxTable.setItems(FXCollections.observableArrayList(notifs));
        }
        updatingInbox = false;

        if (inboxContainer != null) {
            inboxContainer.getChildren().clear();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            for (Notification n : notifs) {
                inboxContainer.getChildren().add(createNotificationCard(n, fmt));
            }
            if (notifs.isEmpty()) {
                Label emptyLabel = new Label("No notifications found.");
                emptyLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
                inboxContainer.getChildren().add(emptyLabel);
            }
        }

        long unread = BankingService.getInstance().getUnreadCount(userId);
        if (unread > 0) {
            unreadBadge.setText(unread + " unread");
            unreadBadge.setVisible(true);
            unreadBadge.setManaged(true);
        } else {
            // Hide badge entirely when everything is read
            unreadBadge.setVisible(false);
            unreadBadge.setManaged(false);
        }
    }

    private HBox createNotificationCard(Notification n, DateTimeFormatter fmt) {
        HBox card = new HBox();
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setSpacing(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 5, 0, 0, 2);");
        
        VBox details = new VBox();
        details.setSpacing(4);
        javafx.scene.layout.HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        
        Label titleLabel = new Label(n.getTitle());
        titleLabel.setStyle("-fx-font-weight: " + (n.isRead() ? "normal" : "bold") + "; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
        
        Label dateLabel = new Label(n.getTimestamp().format(fmt));
        dateLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        
        details.getChildren().addAll(titleLabel, dateLabel);
        
        Label readLabel = new Label(n.isRead() ? "" : "●");
        readLabel.setStyle("-fx-text-fill: #F58220; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(details, readLabel);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> openMessage(n));
        
        return card;
    }

    private void openMessage(Notification n) {
        // Persist read status to backend so unread count updates correctly
        BankingService.getInstance().markNotificationRead(n.getId());
        n.markRead();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");
        messageTitleLabel.setText(n.getTitle());
        messageDateLabel.setText(n.getTimestamp().format(fmt));
        messageBodyArea.setText(n.getMessage());

        messagePanel.setVisible(true);
        messagePanel.setManaged(true);

        if (notificationListPane != null) {
            notificationListPane.setVisible(false);
            notificationListPane.setManaged(false);
        }

        if (placeholderPanel != null) {
            placeholderPanel.setVisible(false);
            placeholderPanel.setManaged(false);
        }

        if (n.getType() == Notification.Type.CARD_APPROVED) {
            messageTitleLabel.getStyleClass().setAll("heading-3");
            messageTitleLabel.setStyle("-fx-text-fill: #166534;");
        } else if (n.getType() == Notification.Type.CARD_REJECTED) {
            messageTitleLabel.getStyleClass().setAll("heading-3");
            messageTitleLabel.setStyle("-fx-text-fill: #991B1B;");
        } else {
            messageTitleLabel.getStyleClass().setAll("heading-3");
            messageTitleLabel.setStyle("-fx-text-fill: #1E293B;");
        }

        // Refresh to update unread badge & row styles.
        // The updatingInbox guard inside refreshInbox() prevents the selection
        // listener from firing again when setItems() clears the selection.
        refreshInbox();
    }

    @FXML
    private void handleMarkAllRead() {
        BankingService.getInstance().markAllNotificationsRead(userId);
        // Clear table selection so the panel resets cleanly
        if (inboxTable != null) {
            inboxTable.getSelectionModel().clearSelection();
        }
        if (messagePanel != null) {
            messagePanel.setVisible(false);
            messagePanel.setManaged(false);
        }
        if (placeholderPanel != null) {
            placeholderPanel.setVisible(true);
            placeholderPanel.setManaged(true);
        }
        if (notificationListPane != null) {
            notificationListPane.setVisible(true);
            notificationListPane.setManaged(true);
        }
        refreshInbox();
    }
}
