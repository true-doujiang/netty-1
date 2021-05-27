package com.yhh.moni;

public interface RejectedExecutionHandler {

    /**
     * Called when someone tried to add a task to {@link SingleThreadEventExecutor} but this failed due capacity
     * restrictions.
     */
    void rejected(Runnable task, SingleThreadEventExecutor executor);
}