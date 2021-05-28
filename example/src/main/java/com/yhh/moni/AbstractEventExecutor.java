package com.yhh.moni;



import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {


    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEventExecutor.class);

    private final EventExecutorGroup parent;



    /**
     * 构造器
     */
    protected AbstractEventExecutor(EventExecutorGroup parent) {
        this.parent = parent;
    }




    @Override
    public EventExecutorGroup parent() {
        return parent;
    }

    @Override
    public EventExecutor next() {
        return this;
    }

    @Override
    public boolean inEventLoop() {
        return inEventLoop(Thread.currentThread());
    }





    /**
     * 太麻烦了  还是用jdk的future吧
     */
    @Override
    public Future<?> submit(Runnable task) {
        return (Future<?>) super.submit(task);
    }


    // 太麻烦了
//    @Override
//    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
//        return new PromiseTask<T>(this, runnable, value);
//    }


    /**
     * @deprecated {@link #shutdownGracefully(long, long, TimeUnit)} or {@link #shutdownGracefully()} instead.
     */
    @Override
    @Deprecated
    public abstract void shutdown();

    /**
     * @deprecated {@link #shutdownGracefully(long, long, TimeUnit)} or {@link #shutdownGracefully()} instead.
     */
    @Override
    @Deprecated
    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.emptyList();
    }


    protected static void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            logger.warn("A task raised an exception. Task: {}", task, t);
        }
    }

}