package com.gabriel.twoforms.services;

import com.gabriel.twoforms.models.Account;
import com.gabriel.twoforms.models.CardRequest;
import com.gabriel.twoforms.models.Notification;
import com.gabriel.twoforms.models.Transaction;
import com.gabriel.twoforms.models.SavingsGoal;
import com.gabriel.twoforms.models.SavingsTransaction;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;

public class BankingService {
    private static BankingService instance;
    private final String baseUrl = "http://localhost:8080/api/banking";
    private final RestTemplate restTemplate;

    private BankingService() {
        restTemplate = new RestTemplate();
        // Register JavaTimeModule to handle Java 8 date/time deserialization
        try {
            for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
                if (restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter) {
                    MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters().get(i);
                    ObjectMapper mapper = converter.getObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                }
            }
        } catch (Throwable t) {
            System.err.println("Could not register JavaTimeModule in BankingService: " + t.getMessage());
        }
    }

    public static BankingService getInstance() {
        if (instance == null) {
            instance = new BankingService();
        }
        return instance;
    }

    public Account createAccount(String customerId, String accountNumber) {
        try {
            String url = baseUrl + "/accounts?customerId=" + customerId + "&accountNumber=" + accountNumber;
            ResponseEntity<Account> response = restTemplate.postForEntity(url, null, Account.class);
            return response.getBody();
        } catch (Exception ex) {
            System.err.println("Failed to create account: " + ex.getMessage());
            return null;
        }
    }

    public Account getAccount(String accountNumber) {
        try {
            String url = baseUrl + "/accounts/" + accountNumber;
            return restTemplate.getForObject(url, Account.class);
        } catch (Exception ex) {
            System.err.println("Failed to fetch account: " + ex.getMessage());
            return null;
        }
    }

    public void deleteAccount(String accountNumber) {
        try {
            String url = baseUrl + "/accounts/" + accountNumber;
            restTemplate.delete(url);
        } catch (Exception ex) {
            System.err.println("Failed to delete account: " + ex.getMessage());
        }
    }

    public void setAccountBalance(String accountNumber, double balance) {
        try {
            String url = baseUrl + "/accounts/" + accountNumber + "/balance?balance=" + balance;
            restTemplate.put(url, null);
        } catch (Exception ex) {
            System.err.println("Failed to set account balance: " + ex.getMessage());
        }
    }

    public List<Account> getAccountsByCustomer(String customerId) {
        try {
            String url = baseUrl + "/accounts/customer/" + customerId;
            ResponseEntity<List<Account>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Account>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch accounts by customer: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Account> getAllAccounts() {
        try {
            String url = baseUrl + "/accounts";
            ResponseEntity<List<Account>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Account>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch all accounts: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean deposit(String accountNumber, double amount, String description) {
        try {
            String url = baseUrl + "/accounts/deposit?accountNumber=" + accountNumber + "&amount=" + amount + "&description=" + description;
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, null, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception ex) {
            System.err.println("Deposit failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean withdraw(String accountNumber, double amount, String description) {
        try {
            String url = baseUrl + "/accounts/withdraw?accountNumber=" + accountNumber + "&amount=" + amount + "&description=" + description;
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, null, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception ex) {
            System.err.println("Withdrawal failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean transfer(String fromAccount, String toAccount, double amount, String description) {
        try {
            String url = baseUrl + "/accounts/transfer?fromAccount=" + fromAccount + "&toAccount=" + toAccount + "&amount=" + amount + "&description=" + description;
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, null, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception ex) {
            System.err.println("Transfer failed: " + ex.getMessage());
            return false;
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        try {
            String url = baseUrl + "/transactions/account/" + accountNumber;
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch transactions: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Transaction> getAllTransactions() {
        try {
            String url = baseUrl + "/transactions";
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch all transactions: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    // Card Requests
    public void submitCardRequest(String customerId, CardRequest.Type type) {
        try {
            String url = baseUrl + "/card-requests?customerId=" + customerId + "&type=" + type;
            restTemplate.postForEntity(url, null, Boolean.class);
        } catch (Exception ex) {
            System.err.println("Failed to submit card request: " + ex.getMessage());
        }
    }

    public List<CardRequest> getCardRequestsByCustomer(String customerId) {
        try {
            String url = baseUrl + "/card-requests/customer/" + customerId;
            ResponseEntity<List<CardRequest>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CardRequest>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch card requests: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<CardRequest> getAllCardRequests() {
        try {
            String url = baseUrl + "/card-requests";
            ResponseEntity<List<CardRequest>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CardRequest>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch all card requests: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public void updateCardRequestStatus(String requestId, CardRequest.Status status) {
        try {
            String url = baseUrl + "/card-requests/" + requestId + "/status?status=" + status;
            restTemplate.put(url, null);
        } catch (Exception ex) {
            System.err.println("Failed to update card request status: " + ex.getMessage());
        }
    }

    // Notifications
    public void sendNotification(String recipientId, String title, String message, Notification.Type type) {
        try {
            String url = baseUrl + "/notifications?recipientId=" + recipientId + "&title=" + title + "&message=" + message + "&type=" + type;
            restTemplate.postForEntity(url, null, Boolean.class);
        } catch (Exception ex) {
            System.err.println("Failed to send notification: " + ex.getMessage());
        }
    }

    public List<Notification> getNotificationsByUser(String userId) {
        try {
            String url = baseUrl + "/notifications/user/" + userId;
            ResponseEntity<List<Notification>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Notification>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch notifications: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public long getUnreadCount(String userId) {
        try {
            String url = baseUrl + "/notifications/user/" + userId + "/unread-count";
            return restTemplate.getForObject(url, Long.class);
        } catch (Exception ex) {
            System.err.println("Failed to get unread count: " + ex.getMessage());
            return 0;
        }
    }

    public boolean markNotificationRead(String notificationId) {
        try {
            String url = baseUrl + "/notifications/" + notificationId + "/read";
            restTemplate.put(url, null);
            return true;
        } catch (Exception ex) {
            System.err.println("Failed to mark notification read: " + ex.getMessage());
            return false;
        }
    }

    public boolean markAllNotificationsRead(String userId) {
        try {
            String url = baseUrl + "/notifications/user/" + userId + "/read-all";
            restTemplate.put(url, null);
            return true;
        } catch (Exception ex) {
            System.err.println("Failed to mark all notifications read: " + ex.getMessage());
            return false;
        }
    }

    // Savings Goals
    public SavingsGoal createSavingsGoal(String customerId, String name, double targetAmount, String description, String targetDate) {
        try {
            String url = baseUrl + "/savings-goals?customerId=" + customerId + "&name=" + name + "&targetAmount=" + targetAmount + "&description=" + description;
            if (targetDate != null && !targetDate.isEmpty()) {
                url += "&targetDate=" + targetDate;
            }
            ResponseEntity<SavingsGoal> response = restTemplate.postForEntity(url, null, SavingsGoal.class);
            return response.getBody();
        } catch (Exception ex) {
            System.err.println("Failed to create savings goal: " + ex.getMessage());
            return null;
        }
    }

    public List<SavingsGoal> getSavingsGoalsByCustomer(String customerId) {
        try {
            String url = baseUrl + "/savings-goals/customer/" + customerId;
            ResponseEntity<List<SavingsGoal>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SavingsGoal>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch savings goals: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public SavingsGoal getSavingsGoal(String id) {
        try {
            String url = baseUrl + "/savings-goals/" + id;
            return restTemplate.getForObject(url, SavingsGoal.class);
        } catch (Exception ex) {
            System.err.println("Failed to fetch savings goal details: " + ex.getMessage());
            return null;
        }
    }

    public void updateSavingsGoalStatus(String id, SavingsGoal.Status status) {
        try {
            String url = baseUrl + "/savings-goals/" + id + "/status?status=" + status;
            restTemplate.put(url, null);
        } catch (Exception ex) {
            System.err.println("Failed to update savings goal status: " + ex.getMessage());
        }
    }

    public boolean depositToSavingsGoal(String id, String accountNumber, double amount) {
        try {
            String url = baseUrl + "/savings-goals/" + id + "/deposit?accountNumber=" + accountNumber + "&amount=" + amount;
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, null, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception ex) {
            System.err.println("Failed to deposit to savings goal: " + ex.getMessage());
            return false;
        }
    }

    public boolean withdrawFromSavingsGoal(String id, String accountNumber, double amount) {
        try {
            String url = baseUrl + "/savings-goals/" + id + "/withdraw?accountNumber=" + accountNumber + "&amount=" + amount;
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, null, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception ex) {
            System.err.println("Failed to withdraw from savings goal: " + ex.getMessage());
            return false;
        }
    }

    public List<SavingsTransaction> getSavingsTransactions(String id) {
        try {
            String url = baseUrl + "/savings-goals/" + id + "/transactions";
            ResponseEntity<List<SavingsTransaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SavingsTransaction>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch savings goal transactions: " + ex.getMessage());
            return new ArrayList<>();
        }
    }
}
