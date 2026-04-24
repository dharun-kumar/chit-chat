package com.chitchat.model;
import java.time.Instant;

public class Chat {
    private final String sender;
    private final String receiver;
    private final String message;
    private final Instant delivertime;

    public Chat(String sender, String receiver, String message, Instant delivertime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.delivertime = delivertime;
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getMessage() { return message; }
    public Instant getDelivertime() { return delivertime; }
}
