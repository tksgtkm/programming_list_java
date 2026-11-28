package com.tickloom.fs;

import com.tickloom.Tickable;

public interface TickableFileIO extends FileIO, Tickable {
    @Override 
    void tick();
}
