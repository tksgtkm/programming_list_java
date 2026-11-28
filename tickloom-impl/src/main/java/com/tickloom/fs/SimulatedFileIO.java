package com.tickloom.fs;

import com.tickloom.future.TickCompletableFuture;
import com.tickloom.fs.TickableFileIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;

public class SimulatedFileIO implements TickableFileIO {
    private final SimulatedFile file;
    private final Deque<Runnable> queue = new ArrayDeque<>();
    private boolean closed;
    private boolean crashed;

    SimulatedFileIO(SimulatedFile file) {
        this.file = file;
    }

    public static SimulatedFileIO standalone() {
        return new SimulatedFileIO(new SimulatedFile());
    }

    @Override 
    public void tick() {
        for (int due = queue.size(); due > 0; due--) {
            queue.poll().run();
        }
    }
    
    @Override 
    public TickCompletableFuture<ByteBuffer> read(long offset, int length) {
        return submit(() -> ByteBuffer.wrap(file.read(offset, length)));
    }

    @Override 
    public TickCompletableFuture<Void> close() {
        TickCompletableFuture<Void> result = submit(() -> null);
        closed = true;
        return result;
    }

    private <T> TickCompletableFuture<T> submit(Callable<T> action) {
        TickCompletableFuture<T> future = new TickCompletableFuture<>();
        if (crashed || closed) {
            future.fail(new IOException(crashed ? "file system crashed" : "file is closed"));
            return future;
        }
        queue.add(() -> {
            T result;
            try {
                result = action.call();
            } catch (Throwable e) {
                future.fail(e);
                return;
            }
            future.complete(result);
        });
        return future;
    }
}
