package com.tickloom.fs;

import com.tickloom.Tickable;

public interface TickableFileSystem extends FileSystem, Tickable {
    @Override 
    void tick();
}
