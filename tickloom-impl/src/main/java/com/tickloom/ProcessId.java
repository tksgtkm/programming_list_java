package com.tickloom;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public record ProcessId(String name) {
    
    public String name() {
        return name;
    }

    public static ProcessId random() {
        return new ProcessId(UUID.randomUUID().toString());
    }

    public static ProcessId of(String name) {
        return new ProcessId(name);
    }

    @Override 
    public String toString() {
        return name;
    }
}
