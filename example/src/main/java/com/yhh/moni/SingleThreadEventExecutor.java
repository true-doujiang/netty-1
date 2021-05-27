package com.yhh.moni;

import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class SingleThreadEventExecutor extends AbstractEventExecutor {


    private final Queue<Runnable> taskQueue;

    // ThreadPerTaskExecutor 创建的线程 就赋值给我了 我就是NioEventLoop中的线程，引擎
    private volatile Thread thread;
    // ThreadPerTaskExecutor: 给我一个任务我就新创建一个FastThreadLocalThread线程去执行任务
    private final Executor executor;


    private final int maxPendingTasks;






    protected SingleThreadEventExecutor(EventExecutorGroup parent,
                                        Executor executor,
                                        boolean addTaskWakesUp,
                                        int maxPendingTasks,
                                        RejectedExecutionHandler rejectedHandler) {
        //
        super(parent);

        this.maxPendingTasks = Math.max(16, maxPendingTasks);

        // executor: ThreadPerTaskExecutor
        this.executor = ObjectUtil.checkNotNull(executor, "executor");

        // 干什么用的: 别的别的线程往这里丢任务   加入荣的是ServerBootstrap中 ServerBootstrapAcceptor
        taskQueue = newTaskQueue(this.maxPendingTasks);
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
    }



    // --------------AbstractExecutorService jdk 方法实现-------------------------

    @Override
    public void execute(Runnable command) {
        // 这个方法很关键了  只要往这里放个任务 就开始给NioEventLoop绑定thread 并启动thread了
        // 把任务放到 taskQueue 队列中
        command.run();
    }





    @Override
    public boolean isShutdown() {
        //return state >= ST_SHUTDOWN;
        return false;
    }

    @Override
    public boolean isTerminated() {
        //return state == ST_TERMINATED;
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }


    @Override
    @Deprecated
    public void shutdown() {
//        if (isShutdown()) {
//            return;
//        }
//
//        boolean inEventLoop = inEventLoop();
//        boolean wakeup;
//        int oldState;
//        for (;;) {
//            if (isShuttingDown()) {
//                return;
//            }
//            int newState;

    }


}
