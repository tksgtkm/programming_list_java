package com.tickloom.network;

public interface MessageCodec {
    
    byte[] encode(Object obj);

    <T> T decode(byte[] data, Class<T> type);
}
