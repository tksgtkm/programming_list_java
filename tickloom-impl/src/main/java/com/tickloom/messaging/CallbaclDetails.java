package com.tickloom.messaging;

import com.tickloom.util.Timeout;

class CallbackDetails {
    private final RequestCallback<?> requestCallback;
    private final Timeout timeout;

    public CallbackDetails(RequestCallback<?> requestCallback, Timeout timeout) {
        this.requestCallback = requestCallback;
        this.timeout = timeout;
    }

    public RequestCallback<?> getRequestCallback() {
        return requestCallback;
    }

    public Timeout getTimeout() {
        return timeout;
    }

    boolean isExpired() {
        return timeout.fired();
    }
}