package com.tickloom.fs;

import com.tickloom.future.TickCompletableFuture;
import java.nio.ByteBuffer;

public interface FileIO {
    
    TickCompletableFuture<ByteBuffer> read(long offset, int length);

    TickCompletableFuture<Integer> write(ByteBuffer buffer, long offset);

    TickCompletableFuture<Void> sync();

    TickCompletableFuture<Long> size();

    TickCompletableFuture<Void> truncate(long newSize);

    TickCompletableFuture<Void> close();
}
