package com.tickloom.fs;

import com.tickloom.fs.FileIO;
import com.tickloom.future.TickCompletableFuture;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class SimulatedFileSystem implements TickableFileSystem {
    private final Deque<Runnable> queue = new ArrayDeque<>();
}
