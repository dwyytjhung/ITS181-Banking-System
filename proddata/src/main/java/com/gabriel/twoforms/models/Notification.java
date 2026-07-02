package com.gabriel.twoforms.models;

import java.time.LocalDateTime;

public class Notification {
    private String id;
    private String recipientId;
    private String title;
    private String message;
    private Type type;
    private boolean read;
    private LocalDateTime timestamp;

    public enum Type {
        CARD_APPROVED, CARD_REJECTED, INFO
    }

    public Notification() {
    }

    public Notification(String id, String recipientId, String title, String message, Type type) {
        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = false;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public void markRead() { this.read = true; }
}
