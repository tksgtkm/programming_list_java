package com.tickloom.fs;

import java.util.Arrays;

final class SimulatedFile {
    private byte[] visible = new byte[0];
    private byte[] durable = new byte[0];

    long size() {
        return visible.length;
    }

    byte[] read(long offset, int length) {
        if (offset >= visible.length) {
            return new byte[0];
        }
        int from = (int)offset;
        return Arrays.copyOfRange(visible, from, Math.min(visible.length, from + length));
    }

    void write(long offset, byte[] data) {
        long end = offset + data.length;
        if (end > visible.length) {
            visible = Arrays.copyOf(visible, (int) end);
        }
        System.arraycopy(data, 0, visible, (int) offset, data.length);
    }

    void truncate(long newSize) {
        if (newSize < visible.length) {
            visible = Arrays.copyOf(visible, (int) newSize);
        }
    }

    void sync() {
        durable = visible.clone();
    }

    void crash() {
        visible = durable.clone();
    }
}
