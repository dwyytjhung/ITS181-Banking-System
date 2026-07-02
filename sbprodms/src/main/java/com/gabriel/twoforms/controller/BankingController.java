package com.gabriel.twoforms.controller;

import com.gabriel.twoforms.entity.*;
import com.gabriel.twoforms.models.*;
import com.gabriel.twoforms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banking")
public class BankingController {

    @Autowired
    private AccountDataRepository accountDataRepository;

    @Autowired
    private TransactionDataRepository transactionDataRepository;

    @Autowired
    private CardRequestDataRepository cardRequestDataRepository;

    @Autowired
    private NotificationDataRepository notificationDataRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private SavingsGoalDataRepository savingsGoalDataRepository;

    @Autowired
    private SavingsTransactionDataRepository savingsTransactionDataRepository;

    // Accounts
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestParam String customerId, @RequestParam String accountNumber) {
        if (accountDataRepository.existsById(accountNumber)) {
            return ResponseEntity.badRequest().body("Account already exists");
        }
        AccountData data = new AccountData();
        data.setAccountNumber(accountNumber);
        data.setCustomerId(customerId);
        data.setBalance(0.0);
        accountDataRepository.save(data);

        Account dto = new Account(accountNumber, customerId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNumber) {
        Optional<AccountData> opt = accountDataRepository.findById(accountNumber);
        if (opt.isPresent()) {
            AccountData a = opt.get();
            Account dto = new Account(a.getAccountNumber(), a.getCustomerId());
            dto.setBalance(a.getBalance());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/accounts/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountNumber) {
        if (accountDataRepository.existsById(accountNumber)) {
            accountDataRepository.deleteById(accountNumber);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/accounts/{accountNumber}/balance")
    public ResponseEntity<?> setAccountBalance(@PathVariable String accountNumber, @RequestParam double balance) {
        Optional<AccountData> opt = accountDataRepository.findById(accountNumber);
        if (opt.isPresent()) {
            AccountData a = opt.get();
            a.setBalance(balance);
            accountDataRepository.save(a);
            Account dto = new Account(a.getAccountNumber(), a.getCustomerId());
            dto.setBalance(a.getBalance());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/accounts/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomer(@PathVariable String customerId) {
        List<Account> list = accountDataRepository.findByCustomerId(customerId).stream()
                .map(a -> {
                    Account dto = new Account(a.getAccountNumber(), a.getCustomerId());
                    dto.setBalance(a.getBalance());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        accountDataRepository.findAll().forEach(a -> {
            Account dto = new Account(a.getAccountNumber(), a.getCustomerId());
            dto.setBalance(a.getBalance());
            list.add(dto);
        });
        return ResponseEntity.ok(list);
    }

    @PostMapping("/accounts/deposit")
    public ResponseEntity<?> deposit(@RequestParam String accountNumber, @RequestParam double amount, @RequestParam String description) {
        Optional<AccountData> opt = accountDataRepository.findById(accountNumber);
        if (opt.isPresent() && amount > 0) {
            AccountData a = opt.get();
            a.setBalance(a.getBalance() + amount);
            accountDataRepository.save(a);

            TransactionData tx = new TransactionData();
            tx.setId(UUID.randomUUID().toString());
            tx.setAccountId(accountNumber);
            tx.setAmount(amount);
            tx.setType(Transaction.Type.DEPOSIT);
            tx.setDescription(description);
            tx.setTimestamp(LocalDateTime.now());
            transactionDataRepository.save(tx);

            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/accounts/withdraw")
    public ResponseEntity<?> withdraw(@RequestParam String accountNumber, @RequestParam double amount, @RequestParam String description) {
        Optional<AccountData> opt = accountDataRepository.findById(accountNumber);
        if (opt.isPresent() && amount > 0) {
            AccountData a = opt.get();
            if (a.getBalance() >= amount) {
                a.setBalance(a.getBalance() - amount);
                accountDataRepository.save(a);

                TransactionData tx = new TransactionData();
                tx.setId(UUID.randomUUID().toString());
                tx.setAccountId(accountNumber);
                tx.setAmount(amount);
                tx.setType(Transaction.Type.WITHDRAWAL);
                tx.setDescription(description);
                tx.setTimestamp(LocalDateTime.now());
                transactionDataRepository.save(tx);

                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<?> transfer(@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam double amount, @RequestParam String description) {
        Optional<AccountData> fromOpt = accountDataRepository.findById(fromAccount);
        Optional<AccountData> toOpt = accountDataRepository.findById(toAccount);

        if (fromOpt.isPresent() && toOpt.isPresent() && amount > 0) {
            AccountData from = fromOpt.get();
            AccountData to = toOpt.get();

            if (from.getBalance() >= amount) {
                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);

                accountDataRepository.save(from);
                accountDataRepository.save(to);

                TransactionData txOut = new TransactionData();
                txOut.setId(UUID.randomUUID().toString());
                txOut.setAccountId(fromAccount);
                txOut.setAmount(amount);
                txOut.setType(Transaction.Type.TRANSFER_OUT);
                txOut.setDescription("To " + toAccount + ": " + description);
                txOut.setTimestamp(LocalDateTime.now());
                transactionDataRepository.save(txOut);

                TransactionData txIn = new TransactionData();
                txIn.setId(UUID.randomUUID().toString());
                txIn.setAccountId(toAccount);
                txIn.setAmount(amount);
                txIn.setType(Transaction.Type.TRANSFER_IN);
                txIn.setDescription("From " + fromAccount + ": " + description);
                txIn.setTimestamp(LocalDateTime.now());
                transactionDataRepository.save(txIn);

                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.badRequest().body(false);
    }

    // Transactions
    @GetMapping("/transactions/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable String accountNumber) {
        List<Transaction> list = transactionDataRepository.findByAccountId(accountNumber).stream()
                .map(t -> {
                    Transaction dto = new Transaction(t.getId(), t.getAccountId(), t.getAmount(), t.getType(), t.getDescription());
                    dto.setTimestamp(t.getTimestamp());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        transactionDataRepository.findAll().forEach(t -> {
            Transaction dto = new Transaction(t.getId(), t.getAccountId(), t.getAmount(), t.getType(), t.getDescription());
            dto.setTimestamp(t.getTimestamp());
            list.add(dto);
        });
        return ResponseEntity.ok(list);
    }

    // Card Requests
    @PostMapping("/card-requests")
    public ResponseEntity<?> submitCardRequest(@RequestParam String customerId, @RequestParam CardRequest.Type type) {
        CardRequestData data = new CardRequestData();
        data.setId(UUID.randomUUID().toString());
        data.setCustomerId(customerId);
        data.setType(type);
        data.setStatus(CardRequest.Status.PENDING);
        data.setRequestDate(LocalDate.now());
        cardRequestDataRepository.save(data);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/card-requests/customer/{customerId}")
    public ResponseEntity<List<CardRequest>> getCardRequestsByCustomer(@PathVariable String customerId) {
        List<CardRequest> list = cardRequestDataRepository.findByCustomerId(customerId).stream()
                .map(c -> {
                    CardRequest dto = new CardRequest(c.getId(), c.getCustomerId(), c.getType());
                    dto.setStatus(c.getStatus());
                    dto.setRequestDate(c.getRequestDate());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/card-requests")
    public ResponseEntity<List<CardRequest>> getAllCardRequests() {
        List<CardRequest> list = new ArrayList<>();
        cardRequestDataRepository.findAll().forEach(c -> {
            CardRequest dto = new CardRequest(c.getId(), c.getCustomerId(), c.getType());
            dto.setStatus(c.getStatus());
            dto.setRequestDate(c.getRequestDate());
            list.add(dto);
        });
        return ResponseEntity.ok(list);
    }

    @PutMapping("/card-requests/{requestId}/status")
    public ResponseEntity<?> updateCardRequestStatus(@PathVariable String requestId, @RequestParam CardRequest.Status status) {
        Optional<CardRequestData> opt = cardRequestDataRepository.findById(requestId);
        if (opt.isPresent()) {
            CardRequestData c = opt.get();
            c.setStatus(status);
            cardRequestDataRepository.save(c);

            // Send notification
            String cardType = c.getType().toString().replace("_", " ");
            if (status == CardRequest.Status.APPROVED) {
                sendNotificationInternal(c.getCustomerId(),
                    "Card Request Approved ✅",
                    "Your " + cardType + " request has been APPROVED by the admin. " +
                    "Your new debit card will be issued shortly. Thank you for banking with PROSPERA!",
                    Notification.Type.CARD_APPROVED);
            } else if (status == CardRequest.Status.REJECTED) {
                sendNotificationInternal(c.getCustomerId(),
                    "Card Request Rejected ❌",
                    "We regret to inform you that your " + cardType + " request has been REJECTED. " +
                    "Please visit a branch or contact support for more information.",
                    Notification.Type.CARD_REJECTED);
            }
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    // Notifications
    @PostMapping("/notifications")
    public ResponseEntity<?> sendNotification(@RequestParam String recipientId, @RequestParam String title, @RequestParam String message, @RequestParam Notification.Type type) {
        sendNotificationInternal(recipientId, title, message, type);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable String userId) {
        List<Notification> list = notificationDataRepository.findByRecipientId(userId).stream()
                .map(n -> {
                    Notification dto = new Notification(n.getId(), n.getRecipientId(), n.getTitle(), n.getMessage(), n.getType());
                    dto.setRead(n.isRead());
                    dto.setTimestamp(n.getTimestamp());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/notifications/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        long count = notificationDataRepository.countByRecipientIdAndRead(userId, false);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        Optional<NotificationData> opt = notificationDataRepository.findById(id);
        if (opt.isPresent()) {
            NotificationData n = opt.get();
            n.setRead(true);
            notificationDataRepository.save(n);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/notifications/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
        List<NotificationData> list = notificationDataRepository.findByRecipientId(userId);
        for (NotificationData n : list) {
            n.setRead(true);
        }
        notificationDataRepository.saveAll(list);
        return ResponseEntity.ok(true);
    }

    private void sendNotificationInternal(String recipientId, String title, String message, Notification.Type type) {
        NotificationData n = new NotificationData();
        n.setId(UUID.randomUUID().toString());
        n.setRecipientId(recipientId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRead(false);
        n.setTimestamp(LocalDateTime.now());
        notificationDataRepository.save(n);
    }

    // Savings Goals
    @PostMapping("/savings-goals")
    public ResponseEntity<?> createSavingsGoal(@RequestParam String customerId, @RequestParam String name, @RequestParam double targetAmount, @RequestParam String description, @RequestParam(required = false) String targetDate) {
        if (savingsGoalDataRepository.existsByCustomerIdAndName(customerId, name)) {
            return ResponseEntity.badRequest().body("A savings goal with this name already exists");
        }
        if (targetAmount <= 0) {
            return ResponseEntity.badRequest().body("Target amount must be greater than zero");
        }
        
        SavingsGoalData data = new SavingsGoalData();
        data.setId(UUID.randomUUID().toString());
        data.setCustomerId(customerId);
        data.setName(name);
        data.setTargetAmount(targetAmount);
        data.setCurrentAmount(0.0);
        data.setDescription(description);
        if (targetDate != null && !targetDate.isEmpty()) {
            data.setTargetDate(LocalDate.parse(targetDate));
        }
        data.setStatus(SavingsGoal.Status.ACTIVE);
        data.setAchievementUnlocked(false);
        savingsGoalDataRepository.save(data);

        SavingsGoal dto = new SavingsGoal(data.getId(), data.getCustomerId(), data.getName(), data.getTargetAmount(), data.getDescription(), data.getTargetDate());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/savings-goals/customer/{customerId}")
    public ResponseEntity<List<SavingsGoal>> getSavingsGoalsByCustomer(@PathVariable String customerId) {
        List<SavingsGoal> list = savingsGoalDataRepository.findByCustomerId(customerId).stream()
                .map(g -> {
                    SavingsGoal dto = new SavingsGoal(g.getId(), g.getCustomerId(), g.getName(), g.getTargetAmount(), g.getDescription(), g.getTargetDate());
                    dto.setCurrentAmount(g.getCurrentAmount());
                    dto.setStatus(g.getStatus());
                    dto.setAchievementUnlocked(g.isAchievementUnlocked());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/savings-goals/{id}")
    public ResponseEntity<?> getSavingsGoal(@PathVariable String id) {
        Optional<SavingsGoalData> opt = savingsGoalDataRepository.findById(id);
        if (opt.isPresent()) {
            SavingsGoalData g = opt.get();
            SavingsGoal dto = new SavingsGoal(g.getId(), g.getCustomerId(), g.getName(), g.getTargetAmount(), g.getDescription(), g.getTargetDate());
            dto.setCurrentAmount(g.getCurrentAmount());
            dto.setStatus(g.getStatus());
            dto.setAchievementUnlocked(g.isAchievementUnlocked());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/savings-goals/{id}/status")
    public ResponseEntity<?> updateSavingsGoalStatus(@PathVariable String id, @RequestParam SavingsGoal.Status status) {
        Optional<SavingsGoalData> opt = savingsGoalDataRepository.findById(id);
        if (opt.isPresent()) {
            SavingsGoalData g = opt.get();
            g.setStatus(status);
            savingsGoalDataRepository.save(g);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/savings-goals/{id}/deposit")
    public ResponseEntity<?> depositToSavingsGoal(@PathVariable String id, @RequestParam String accountNumber, @RequestParam double amount) {
        Optional<SavingsGoalData> goalOpt = savingsGoalDataRepository.findById(id);
        Optional<AccountData> accOpt = accountDataRepository.findById(accountNumber);

        if (goalOpt.isPresent() && accOpt.isPresent() && amount > 0) {
            SavingsGoalData goal = goalOpt.get();
            AccountData acc = accOpt.get();

            if (acc.getBalance() >= amount) {
                // Perform transfer
                acc.setBalance(acc.getBalance() - amount);
                goal.setCurrentAmount(goal.getCurrentAmount() + amount);

                // Auto-completion detection
                if (goal.getCurrentAmount() >= goal.getTargetAmount() && goal.getStatus() == SavingsGoal.Status.ACTIVE) {
                    goal.setStatus(SavingsGoal.Status.COMPLETED);
                    goal.setAchievementUnlocked(true);
                    
                    sendNotificationInternal(goal.getCustomerId(),
                        "Savings Goal Achieved 🏆",
                        "Congratulations! You achieved your \"" + goal.getName() + "\" Savings Goal. Keep it up!",
                        Notification.Type.INFO);
                }

                accountDataRepository.save(acc);
                savingsGoalDataRepository.save(goal);

                // Record banking transaction history
                TransactionData tx = new TransactionData();
                tx.setId(UUID.randomUUID().toString());
                tx.setAccountId(accountNumber);
                tx.setAmount(amount);
                tx.setType(Transaction.Type.TRANSFER_OUT);
                tx.setDescription("Transfer to Vault: " + goal.getName());
                tx.setTimestamp(LocalDateTime.now());
                transactionDataRepository.save(tx);

                // Record savings transaction history
                SavingsTransactionData stx = new SavingsTransactionData();
                stx.setId(UUID.randomUUID().toString());
                stx.setGoalId(id);
                stx.setAmount(amount);
                stx.setType(SavingsTransaction.Type.DEPOSIT);
                stx.setTimestamp(LocalDateTime.now());
                stx.setUpdatedBalance(goal.getCurrentAmount());
                savingsTransactionDataRepository.save(stx);

                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/savings-goals/{id}/withdraw")
    public ResponseEntity<?> withdrawFromSavingsGoal(@PathVariable String id, @RequestParam String accountNumber, @RequestParam double amount) {
        Optional<SavingsGoalData> goalOpt = savingsGoalDataRepository.findById(id);
        Optional<AccountData> accOpt = accountDataRepository.findById(accountNumber);

        if (goalOpt.isPresent() && accOpt.isPresent() && amount > 0) {
            SavingsGoalData goal = goalOpt.get();
            AccountData acc = accOpt.get();

            if (goal.getCurrentAmount() >= amount) {
                // Perform transfer
                goal.setCurrentAmount(goal.getCurrentAmount() - amount);
                acc.setBalance(acc.getBalance() + amount);

                // If balance falls below target, revert status back to ACTIVE if it was COMPLETED
                if (goal.getCurrentAmount() < goal.getTargetAmount() && goal.getStatus() == SavingsGoal.Status.COMPLETED) {
                    goal.setStatus(SavingsGoal.Status.ACTIVE);
                }

                accountDataRepository.save(acc);
                savingsGoalDataRepository.save(goal);

                // Record banking transaction history
                TransactionData tx = new TransactionData();
                tx.setId(UUID.randomUUID().toString());
                tx.setAccountId(accountNumber);
                tx.setAmount(amount);
                tx.setType(Transaction.Type.TRANSFER_IN);
                tx.setDescription("Withdrawal from Vault: " + goal.getName());
                tx.setTimestamp(LocalDateTime.now());
                transactionDataRepository.save(tx);

                // Record savings transaction history
                SavingsTransactionData stx = new SavingsTransactionData();
                stx.setId(UUID.randomUUID().toString());
                stx.setGoalId(id);
                stx.setAmount(amount);
                stx.setType(SavingsTransaction.Type.WITHDRAWAL);
                stx.setTimestamp(LocalDateTime.now());
                stx.setUpdatedBalance(goal.getCurrentAmount());
                savingsTransactionDataRepository.save(stx);

                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/savings-goals/{id}/transactions")
    public ResponseEntity<List<SavingsTransaction>> getSavingsTransactions(@PathVariable String id) {
        List<SavingsTransaction> list = savingsTransactionDataRepository.findByGoalIdOrderByTimestampDesc(id).stream()
                .map(t -> {
                    SavingsTransaction dto = new SavingsTransaction(t.getId(), t.getGoalId(), t.getAmount(), t.getType(), t.getUpdatedBalance());
                    dto.setTimestamp(t.getTimestamp());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
