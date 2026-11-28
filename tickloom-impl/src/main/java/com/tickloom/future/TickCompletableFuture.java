package com.tickloom.future;

import com.tickloom.Continuation;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <T>
 * TickCompletableFuture
 */
public class TickCompletableFuture<T> {
    
    public <U> TickCompletableFuture<U> thenApply(Function<T, U> fn) {
        TickCompletableFuture<U> mapped = new TickCompletableFuture<>();
        this.whenComplete((T result, Throwable exception) -> {
            if (exception != null) {
                mapped.fail(exception);
                return;
            }
            try {
                mapped.complete(fn.apply(result));
            } catch (Exception e) {
                mapped.fail(e);
            }
        });
        return mapped;
    }

    public <U> TickCompletableFuture<U> thenCompose(Function<T, TickCompletableFuture<U>> fn) {
        TickCompletableFuture<U> nextStage = new TickCompletableFuture<>();

        this.whenComplete((T result, Throwable exception) -> {
            if (exception != null) {
                nextStage.fail(exception);
                return;
            }
            try {
                TickCompletableFuture<U> innerFuture = fn.apply(result);
                handleInnerFutureAndCompleteStage(innerFuture, nextStage);
            } catch (Exception e) {
                nextStage.fail(e);
            }
        });
        return nextStage;
    }

    private <U> void handleInnerFutureAndCompleteStage(TickCompletableFuture<U> innFuture, TickCompletableFuture<U> nextStage) {
        innFuture.whenComplete((U innerResult, Throwable innerException) -> {
            if (innerException != null) {
                nextStage.fail(innerException);
            } else {
                nextStage.complete(innerResult);
            }
        });
    }

    public TickCompletableFuture<T> whenComplete(Continuation<T> c) {
        return this.whenComplete((result, exception) -> {
            if (exception == null) {
                c.resume(result);
            } else {
                c.resumeWithError(exception);
            }
        });
    }

    private enum State {
        PENDING,
        COMPLETED,
        FAILED
    }

    private final Thread ownerThread = Thread.currentThread();
    private State state = State.PENDING;
    private T result;
    private Throwable exception;
    private BiConsumer<T, Throwable> callback;

    private void checkOnOwnerThread() {
        if (Thread.currentThread() != ownerThread) {
            throw new IllegalStateException(
                "TickCompletableFuture must be accessed on its owner (tick) thread ["
                    + ownerThread.getName() + "], but was called from ["
                    + Thread.currentThread().getName() + "]"
            );
        }
    }

    public static <T> TickCompletableFuture<T> completed(T value) {
        TickCompletableFuture<T> future = new TickCompletableFuture<>();
        future.complete(value);
        return future;
    }

    public TickCompletableFuture() {

    }

    public boolean isPending() {
        return state == State.PENDING;
    }

    public boolean isCompleted() {
        return state == State.COMPLETED;
    }

    public boolean isFailed() {
        return state == State.FAILED;
    }

    public T getResult() {
        if (state != State.COMPLETED) {
            throw new IllegalStateException("Future is not completed successfully");
        }
        return result;
    }

    public Throwable getException() {
        if (state != State.FAILED) {
            throw new IllegalStateException("Future has not failed");
        }
        return exception;
    }

    public void complete(T result) {
        checkOnOwnerThread();
        if (state != State.PENDING) {
            throw new IllegalStateException("Future is already resolved");
        }
        this.state = State.COMPLETED;
        this.result = result;

        if (callback != null) {
            callback.accept(result, null);
            callback = null;
        }
    }

    public void fail(Throwable exception) {
        checkOnOwnerThread();
        if (state != State.PENDING) {
            throw new IllegalStateException("Future is already resolved");
        }
        this.state = State.FAILED;
        this.exception = exception;

        if (callback != null) {
            callback.accept(null, exception);
            callback = null;
        }
    }

    public TickCompletableFuture<T> whenComplete(BiConsumer<T, Throwable> callback) {
        checkOnOwnerThread();
        TickCompletableFuture<T> nextStage = new TickCompletableFuture<>();

        BiConsumer<T, Throwable> wrapperCallback = (res, ex) -> {
            try {
                callback.accept(res, ex);
                if (ex != null) {
                    nextStage.fail(ex);
                } else {
                    nextStage.complete(res);
                }
            } catch (Throwable t) {
                nextStage.fail(t);
            }
        };

        if (state == State.COMPLETED) {
            wrapperCallback.accept(result, null);
        } else if (state == State.FAILED) {
            wrapperCallback.accept(null, exception);
        } else {
            if (this.callback != null) {
                throw new IllegalStateException("Only a single callback is supported for pending future");
            }
            this.callback = wrapperCallback;
        }
        return nextStage;
    }
}
