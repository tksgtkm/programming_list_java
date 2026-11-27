package com.tickloom.util;

public final class Timeout {
    private final String name;
    private final long durationTicks;
    private long ticks = 0;
    private boolean ticking = false;
    private long startTick = 0;

    public Timeout(String name, long durationTicks) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or enpty");
        }
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        this.name = name;
        this.durationTicks = durationTicks;
    }

    public void start() {
        ticking = true;
        startTick = ticks;
    }

    public void tick() {
        ticks++;
    }

    public boolean fired() {
        if (!ticking) {
            return false;
        }
        return (ticks - startTick) >= durationTicks;
    }

    public void reset() {
        startTick = ticks;
    }

    public void stop() {
        ticking = false;
    }

    public String getName() {
        return name;
    }

    public long getDurationTicks() {
        return durationTicks;
    }

    public long getTicks() {
        return ticks;
    }

    public boolean isTicking() {
        return ticking;
    }

    public long getRemainingTicks() {
        if (!ticking) {
            return 0;
        }
        long elapsed = ticks - startTick;
        return Math.max(0, durationTicks - elapsed);
    }

    @Override 
    public String toString() {
        return String.format("Timeout[name=%s, duration=%d, ticking=%s, remaining=%d]",
            name, durationTicks, ticking, getRemainingTicks()
        );
    }
}
