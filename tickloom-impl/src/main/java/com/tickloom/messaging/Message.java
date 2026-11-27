package com.tickloom.messaging;

import com.tickloom.ProcessId;
import com.tickloom.network.PeerType;

public record Message(
    ProcessId source,
    ProcessId destination,
    PeerType peerType,
    MessageType messageType,
    byte[] payload,
    String correlationId
) {
    
    public Message {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }
        if (peerType == null) {
            throw new IllegalArgumentException("Peer type cannot be null");
        }
        if (messageType == null) {
            throw new IllegalArgumentException("Message type cannot be null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
    }

    public static Message of(
        ProcessId source, 
        ProcessId destination, 
        PeerType peerType, 
        MessageType messageType, 
        byte[] payload, 
        String correlationId
    ) {
        return new Message(source, destination, peerType,messageType, payload, correlationId);
    }
}
