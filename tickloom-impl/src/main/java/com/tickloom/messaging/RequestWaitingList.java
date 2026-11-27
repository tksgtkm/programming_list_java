package com.tickloom.messaging;

import com.tickloom.ProcessId;
import com.tickloom.util.Timeout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 
 * RequestWaitingList
 * @param <Key>
 * @param <Response>
 */
public class RequestWaitingList<Key, Response> {
    
    private final Map<Key, CallbackDetails> pendingRequests = new LinkedHashMap<>();
    private final long expirationTicks;

    /**
     * @param expirationTicks
     */
    public RequestWaitingList(long expirationTicks) {
        this.expirationTicks = expirationTicks;
    }

    /**
     * 
     * @param key
     * @param callback
     */
    public void add(Key key, RequestCallback<Response> callback) {
        Timeout timeout = new Timeout("request-" + key, expirationTicks);
        timeout.start();
        pendingRequests.put(key, new CallbackDetails(callback, timeout));
    }

    /**
     * 
     * @param key
     * @param response
     */
    public void handleResponse(Key key, Response response, ProcessId fromNode) {
        if (!pendingRequests.containsKey(key)) {
            return;
        }

        CallbackDetails callbackDetails = pendingRequests.remove(key);
        @SuppressWarnings("unchecked")
        RequestCallback<Response> callback = (RequestCallback<Response>) callbackDetails.getRequestCallback();
        callback.onResponse(response, fromNode);
    }

    /**
     * 
     * @param key
     * @param error
     */
    public void handleError(Key key, Exception error) {
        CallbackDetails callbackDetails = pendingRequests.remove(key);
        if (callbackDetails != null) {
            callbackDetails.getRequestCallback().onError(error);
        }
    }

    public void tick() {
        pendingRequests.values().forEach(callbackDetails ->
            callbackDetails.getTimeout().tick()
        );

        List<Key> expiredRequestKeys = getExpiredRequestKeys();
        if (expiredRequestKeys.isEmpty()) {
            return;
        }

        expiredRequestKeys.forEach(expiredRequestKey -> {
            CallbackDetails callbackDetails = pendingRequests.remove(expiredRequestKey);
            if (callbackDetails != null) {
                callbackDetails.getRequestCallback().onError(
                    new TimeoutException("Request expired after " + expirationTicks + " ticks")
                );
            }
        });
    }

    private List<Key> getExpiredRequestKeys() {
        return pendingRequests.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public int size() {
        return pendingRequests.size();
    }

    public boolean isEmpty() {
        return pendingRequests.isEmpty();
    }

    public void clear() {
        pendingRequests.forEach((key, callbackDetails) -> {
            callbackDetails.getRequestCallback().onError(
                new RuntimeException("Request cancelled due to shutdown")
            );
        });
        pendingRequests.clear();
    }
}
