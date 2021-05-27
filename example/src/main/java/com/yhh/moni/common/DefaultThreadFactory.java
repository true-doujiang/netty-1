package com.yhh.moni.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link ThreadFactory} implementation with a simple naming rule.
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolId = new AtomicInteger();

    private final AtomicInteger nextId = new AtomicInteger();
    private final String prefix;


    /**
     * 构造器
     */
    public DefaultThreadFactory(Class<?> poolType, int priority) {
        this(poolType, false, priority);
    }

    public DefaultThreadFactory(Class<?> poolType, boolean daemon, int priority) {
        this(poolType.getSimpleName(), daemon, priority);
    }

    /**
     *
     */
    public DefaultThreadFactory(String poolName, boolean daemon, int priority) {
        if (poolName == null) {
            throw new NullPointerException("poolName");
        }

        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(
                    "priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }

        prefix = poolName + '-' + poolId.incrementAndGet() + '-';
    }


    /**
     *
     */
    @Override
    public Thread newThread(Runnable r) {
        // 这个线程会赋值配NioEventLoop 中的 thread
        Runnable wrap = r;
        Thread t = newThread(wrap, prefix + nextId.incrementAndGet());
        return t;
    }

    protected Thread newThread(Runnable r, String name) {
        return new Thread(r, name);
    }
}
