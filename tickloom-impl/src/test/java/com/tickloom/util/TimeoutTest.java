package com.tickloom.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TimeoutTest {

    @Test 
    void doesNotFireBeforeStart() {
        Timeout t = new Timeout("t", 3);
        for (int i = 0; i < 10; i++) {
            t.tick();
        }
        assertFalse(t.fired());
    }

    @Test
    void firesExactlyAfterDurationTicks() {
        Timeout t = new Timeout("t", 5);
        t.start();
        for (int i = 0; i < 4; i++) {
            t.tick();
            assertFalse(t.fired(), "should not fire at tick " + (i + 1));
        }
        t.tick();
        assertTrue(t.fired());
        assertEquals(0, t.getRemainingTicks());
    }

    @Test
    void startCountsFromCurrentTick() {
        Timeout t = new Timeout("t", 3);
        for (int i = 0; i < 100; i++) t.tick();  // 開始前に進んでいても影響しない
        t.start();
        t.tick(); t.tick();
        assertFalse(t.fired());
        t.tick();
        assertTrue(t.fired());
    }

    @Test
    void resetRestartsCountdown() {
        Timeout t = new Timeout("t", 2);
        t.start();
        t.tick(); t.tick();
        assertTrue(t.fired());
        t.reset();
        assertFalse(t.fired());
        assertEquals(2, t.getRemainingTicks());
        t.tick(); t.tick();
        assertTrue(t.fired());
    }

    @Test
    void stopPreventsFiring() {
        Timeout t = new Timeout("t", 1);
        t.start();
        t.stop();
        t.tick();
        assertFalse(t.fired());
        assertFalse(t.isTicking());
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Timeout("", 1));
        assertThrows(IllegalArgumentException.class, () -> new Timeout("t", 0));
    }
}