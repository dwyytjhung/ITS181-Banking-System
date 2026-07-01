package com.gabriel.twoforms.models;

import java.time.LocalDate;

public class CardRequest {
    private String id;
    private String customerId;
    private Type type;
    private Status status;
    private LocalDate requestDate;

    public enum Type {
        NEW_CARD, LOST_CARD, DAMAGED_CARD
    }

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public CardRequest(String id, String customerId, Type type) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.status = Status.PENDING;
        this.requestDate = LocalDate.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Type getType() { return type; }
    public Status getStatus() { return status; }
    public LocalDate getRequestDate() { return requestDate; }

    public void setStatus(Status status) {
        this.status = status;
    }
}
