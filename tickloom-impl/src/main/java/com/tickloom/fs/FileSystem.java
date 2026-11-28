package com.tickloom.fs;

import com.tickloom.fs.FileIO;
import com.tickloom.future.TickCompletableFuture;

import java.util.List;

public interface FileSystem {
    TickCompletableFuture<FileIO> open(String name, OpenMode mode);

    TickCompletableFuture<Boolean> exists(String name);

    TickCompletableFuture<List<String>> list();

    TickCompletableFuture<Void> delete(String name);

    TickCompletableFuture<Void> rename(String from, String to);

    TickCompletableFuture<Void> syncDirectory();
}
