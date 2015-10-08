package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

/* package */ class DbRunnable<T> implements Runnable {

    private final BlockingQueue<Tuple<DBOperation<T>, CompletableFuture<T>>> queue;

    /* package */ DbRunnable() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /* package */ CompletableFuture<T> add(DBOperation<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        queue.add(new Tuple<>(callable, future));
        return future;
    }

    @Override
    public void run() {
        Tuple<DBOperation<T>, CompletableFuture<T>> tuple;

        while(!Thread.interrupted()) {
            try {
                tuple = queue.take();
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            try {
                tuple.second.complete(tuple.first.call());
            } catch(Exception e) {
                tuple.second.completeExceptionally(e);
            }
        }
    }

}
