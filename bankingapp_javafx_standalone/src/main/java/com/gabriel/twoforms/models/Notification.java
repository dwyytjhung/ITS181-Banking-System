package com.gabriel.twoforms.models;

import java.time.LocalDateTime;

public class Notification {
    private final String id;
    private final String recipientId;
    private final String title;
    private final String message;
    private final Type type;
    private boolean read;
    private final LocalDateTime timestamp;

    public enum Type {
        CARD_APPROVED, CARD_REJECTED, INFO
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
    public String getRecipientId() { return recipientId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Type getType() { return type; }
    public boolean isRead() { return read; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void markRead() { this.read = true; }
}
