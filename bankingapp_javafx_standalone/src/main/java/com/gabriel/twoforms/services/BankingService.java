package com.gabriel.twoforms.services;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.CardRequest;
import com.gabriel.twoforms.models.Notification;
import com.gabriel.twoforms.models.Transaction;
import com.gabriel.twoforms.models.Transaction.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BankingService {
    private static BankingService instance;
    private final List<Account> accounts;
    private final List<Transaction> transactions;
    private final List<CardRequest> cardRequests;
    private final List<Notification> notifications;

    private BankingService() {
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
        cardRequests = new ArrayList<>();
        notifications = new ArrayList<>();
        
        // Seed data
        createAccount("U2", "100010001");
        createAccount("U3", "100010002");
        
        deposit("100010001", 5000.0, "Initial Deposit");
        deposit("100010002", 3000.0, "Initial Deposit");
    }

    public static BankingService getInstance() {
        if (instance == null) {
            instance = new BankingService();
        }
        return instance;
    }

    public Account createAccount(String customerId, String accountNumber) {
        if (getAccount(accountNumber) != null) return null;
        Account account = new Account(accountNumber, customerId);
        accounts.add(account);
        return account;
    }

    public Account getAccount(String accountNumber) {
        return accounts.stream().filter(a -> a.getAccountNumber().equals(accountNumber)).findFirst().orElse(null);
    }
    
    public void deleteAccount(String accountNumber) {
        accounts.removeIf(a -> a.getAccountNumber().equals(accountNumber));
    }
    
    public void setAccountBalance(String accountNumber, double balance) {
        Account acc = getAccount(accountNumber);
        if (acc != null) {
            // Because balance is mostly updated via deposit/withdraw, we can do a hack for admin:
            if (acc.getBalance() < balance) {
                acc.deposit(balance - acc.getBalance());
            } else if (acc.getBalance() > balance) {
                acc.withdraw(acc.getBalance() - balance);
            }
        }
    }
    
    public List<Account> getAccountsByCustomer(String customerId) {
        return accounts.stream().filter(a -> a.getCustomerId().equals(customerId)).collect(Collectors.toList());
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public boolean deposit(String accountNumber, double amount, String description) {
        Account acc = getAccount(accountNumber);
        if (acc != null && amount > 0) {
            acc.deposit(amount);
            transactions.add(new Transaction(UUID.randomUUID().toString(), accountNumber, amount, Type.DEPOSIT, description));
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount, String description) {
        Account acc = getAccount(accountNumber);
        if (acc != null && acc.withdraw(amount)) {
            transactions.add(new Transaction(UUID.randomUUID().toString(), accountNumber, amount, Type.WITHDRAWAL, description));
            return true;
        }
        return false;
    }

    public boolean transfer(String fromAccount, String toAccount, double amount, String description) {
        Account from = getAccount(fromAccount);
        Account to = getAccount(toAccount);
        
        if (from != null && to != null && from.withdraw(amount)) {
            to.deposit(amount);
            transactions.add(new Transaction(UUID.randomUUID().toString(), fromAccount, amount, Type.TRANSFER_OUT, "To " + toAccount + ": " + description));
            transactions.add(new Transaction(UUID.randomUUID().toString(), toAccount, amount, Type.TRANSFER_IN, "From " + fromAccount + ": " + description));
            return true;
        }
        return false;
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactions.stream().filter(t -> t.getAccountId().equals(accountNumber)).collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    // Card Requests
    public void submitCardRequest(String customerId, CardRequest.Type type) {
        cardRequests.add(new CardRequest(UUID.randomUUID().toString(), customerId, type));
    }

    public List<CardRequest> getCardRequestsByCustomer(String customerId) {
        return cardRequests.stream().filter(c -> c.getCustomerId().equals(customerId)).collect(Collectors.toList());
    }

    public List<CardRequest> getAllCardRequests() {
        return new ArrayList<>(cardRequests);
    }

    public void updateCardRequestStatus(String requestId, CardRequest.Status status) {
        cardRequests.stream()
            .filter(c -> c.getId().equals(requestId))
            .findFirst()
            .ifPresent(c -> {
                c.setStatus(status);
                // Send notification to the customer
                String cardType = c.getType().toString().replace("_", " ");
                if (status == CardRequest.Status.APPROVED) {
                    sendNotification(c.getCustomerId(),
                        "Card Request Approved ✅",
                        "Your " + cardType + " request has been APPROVED by the admin. " +
                        "Your new debit card will be issued shortly. Thank you for banking with PROSPERA!",
                        Notification.Type.CARD_APPROVED);
                } else if (status == CardRequest.Status.REJECTED) {
                    sendNotification(c.getCustomerId(),
                        "Card Request Rejected ❌",
                        "We regret to inform you that your " + cardType + " request has been REJECTED. " +
                        "Please visit a branch or contact support for more information.",
                        Notification.Type.CARD_REJECTED);
                }
            });
    }

    // Notifications
    public void sendNotification(String recipientId, String title, String message, Notification.Type type) {
        notifications.add(new Notification(UUID.randomUUID().toString(), recipientId, title, message, type));
    }

    public List<Notification> getNotificationsByUser(String userId) {
        return notifications.stream()
                .filter(n -> n.getRecipientId().equals(userId))
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String userId) {
        return notifications.stream()
                .filter(n -> n.getRecipientId().equals(userId) && !n.isRead())
                .count();
    }
}
