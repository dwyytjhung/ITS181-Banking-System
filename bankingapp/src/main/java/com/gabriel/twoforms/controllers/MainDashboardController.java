package com.gabriel.twoforms.controllers;

import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.services.AuthService;
import com.gabriel.twoforms.utils.ViewLoader;
import com.gabriel.twoforms.utils.ViewModeManager;
import com.gabriel.twoforms.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class MainDashboardController {

    private static MainDashboardController activeInstance;

    public static MainDashboardController getActiveInstance() {
        return activeInstance;
    }

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label userNameLabel;

    @FXML
    private VBox sidebarMenu;

    @FXML
    private StackPane contentArea;

    // Mobile Bottom Nav Buttons (Customer)
    @FXML
    private Button navDashboard;
    @FXML
    private Button navTransfers;
    @FXML
    private Button navTransactions;
    @FXML
    private Button navCards;
    @FXML
    private Button navProfile;

    // Mobile Bottom Nav Buttons (Admin)
    @FXML
    private Button adminNavDashboard;
    @FXML
    private Button adminNavAccounts;
    @FXML
    private Button adminNavCards;
    @FXML
    private Button adminNavProfile;

    @FXML
    private HBox customerNavBar;

    @FXML
    private HBox adminNavBar;

    private User currentUser;

    @FXML
    public void initialize() {
        activeInstance = this;
        currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (userNameLabel != null) {
                userNameLabel.setText(currentUser.getFullName());
            }

            if (sidebarMenu != null) {
                buildSidebar();
            }

            if (customerNavBar != null && adminNavBar != null) {
                if (currentUser.getRole() == User.Role.ADMIN) {
                    customerNavBar.setVisible(false);
                    customerNavBar.setManaged(false);
                    adminNavBar.setVisible(true);
                    adminNavBar.setManaged(true);
                } else {
                    customerNavBar.setVisible(true);
                    customerNavBar.setManaged(true);
                    adminNavBar.setVisible(false);
                    adminNavBar.setManaged(false);
                }
            }

            String activeView = ViewModeManager.getActiveViewFxml();
            if (activeView == null) {
                activeView = (currentUser.getRole() == User.Role.ADMIN) ? "admin_dashboard.fxml" : "customer_dashboard.fxml";
            }
            loadView(activeView);
            highlightNav(activeView);
        }
    }

    private void buildSidebar() {
        sidebarMenu.getChildren().clear();
        String activeView = ViewModeManager.getActiveViewFxml();
        if (activeView == null) {
            activeView = (currentUser.getRole() == User.Role.ADMIN) ? "admin_dashboard.fxml" : "customer_dashboard.fxml";
        }
        
        if (currentUser.getRole() == User.Role.ADMIN) {
            sidebarMenu.getChildren().add(createMenuButton("Dashboard", "admin_dashboard.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Manage Accounts", "manage_accounts.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Card Requests", "admin_card_requests.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Profile & Settings", "profile.fxml", activeView));
        } else {
            sidebarMenu.getChildren().add(createMenuButton("Dashboard", "customer_dashboard.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Deposit / Withdraw", "deposit_withdraw.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Transfer Money", "transfer.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Transactions", "transactions.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Statements & Receipts", "statements.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Card Center", "customer_card_center.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Inbox", "customer_inbox.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Profile & Settings", "profile.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Loan Calculator", "loan_calculator.fxml", activeView));
            sidebarMenu.getChildren().add(createMenuButton("Currency Exchange", "currency_exchange.fxml", activeView));
        }
    }

    private Button createMenuButton(String text, String fxml, String activeFxml) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-button");
        if (fxml.equals(activeFxml)) {
            btn.getStyleClass().add("sidebar-button-active");
        }
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            for(Node node : sidebarMenu.getChildren()) {
                node.getStyleClass().remove("sidebar-button-active");
            }
            btn.getStyleClass().add("sidebar-button-active");
            ViewModeManager.setActiveViewFxml(fxml);
            loadView(fxml);
        });
        return btn;
    }

    public void navigateToView(String fxml) {
        ViewModeManager.setActiveViewFxml(fxml);
        loadView(fxml);
        if (sidebarMenu != null) {
            buildSidebar();
        }
        highlightNav(fxml);
    }

    private void highlightNav(String fxml) {
        // Clear all active highlights for mobile nav buttons
        Button[] btns = (currentUser.getRole() == User.Role.ADMIN)
            ? new Button[]{adminNavDashboard, adminNavAccounts, adminNavCards, adminNavProfile}
            : new Button[]{navDashboard, navTransfers, navTransactions, navCards, navProfile};

        for (Button btn : btns) {
            if (btn != null) {
                btn.getStyleClass().remove("mobile-nav-active");
            }
        }

        // Highlight target button
        Button target = null;
        if (currentUser.getRole() == User.Role.ADMIN) {
            if ("admin_dashboard.fxml".equals(fxml)) target = adminNavDashboard;
            else if ("manage_accounts.fxml".equals(fxml)) target = adminNavAccounts;
            else if ("admin_card_requests.fxml".equals(fxml)) target = adminNavCards;
            else if ("profile.fxml".equals(fxml)) target = adminNavProfile;
        } else {
            if ("customer_dashboard.fxml".equals(fxml)) target = navDashboard;
            else if ("transfer.fxml".equals(fxml)) target = navTransfers;
            else if ("transactions.fxml".equals(fxml)) target = navTransactions;
            else if ("customer_card_center.fxml".equals(fxml)) target = navCards;
            else if ("profile.fxml".equals(fxml)) target = navProfile;
        }

        if (target != null) {
            target.getStyleClass().add("mobile-nav-active");
        }
    }

    private void loadView(String fxml) {
        ViewLoader.loadView(contentArea, fxml);
    }

    // Customer Bottom Navigation Click Handlers
    @FXML
    private void handleMobileNavDashboard() {
        navigateToView("customer_dashboard.fxml");
    }

    @FXML
    private void handleMobileNavTransfers() {
        navigateToView("transfer.fxml");
    }

    @FXML
    private void handleMobileNavTransactions() {
        navigateToView("transactions.fxml");
    }

    @FXML
    private void handleMobileNavCards() {
        navigateToView("customer_card_center.fxml");
    }

    @FXML
    private void handleMobileNavProfile() {
        navigateToView("profile.fxml");
    }

    // Admin Bottom Navigation Click Handlers
    @FXML
    private void handleAdminMobileNavDashboard() {
        navigateToView("admin_dashboard.fxml");
    }

    @FXML
    private void handleAdminMobileNavAccounts() {
        navigateToView("manage_accounts.fxml");
    }

    @FXML
    private void handleAdminMobileNavCards() {
        navigateToView("admin_card_requests.fxml");
    }

    @FXML
    private void handleAdminMobileNavProfile() {
        navigateToView("profile.fxml");
    }

    @FXML
    private void handleLogout() {
        boolean confirmed = DialogUtil.confirm(
            "Confirm Logout",
            "Are you sure you want to log out?",
            "You will be signed out of your session and returned to the welcome screen.",
            "Yes, Log Out"
        );
        if (!confirmed) return;

        AuthService.getInstance().logout();
        ViewModeManager.setActiveViewFxml(null); // Reset active view upon logout
        Stage stage = (Stage) rootPane.getScene().getWindow();
        ViewLoader.loadScene(stage, "login.fxml", "PROSPERA - Log In");
    }
}
