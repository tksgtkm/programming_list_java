package com.tickloom.messaging;

import java.util.Objects;

public record MessageType(String name) {
    
    public static final MessageType HELLO = new MessageType("HELLO");
    public static final MessageType WELCOME = new MessageType("WELCOME");
    public static final MessageType REJECT = new MessageType("REJECT");

    public MessageType(String name) {
        this.name = Objects.requireNonNull(name, "Message type name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Message type name cannot be blank");
        }
    }

    @Override 
    public String name() {
        return name;
    }

    public static MessageType of(String name) {
        return new MessageType(name);
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MessageType that = (MessageType) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
