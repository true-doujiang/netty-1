package com.yhh.moni;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class SingleThreadEventExecutor extends AbstractEventExecutor {


    // NioEventLoop中的Thread 状态
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;

    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");

    private volatile int state = ST_NOT_STARTED;

    private final Queue<Runnable> taskQueue;

    // ThreadPerTaskExecutor 创建的线程 就赋值给我了 我就是NioEventLoop中的线程，引擎
    private volatile Thread thread;
    // ThreadPerTaskExecutor: 给我一个任务我就新创建一个FastThreadLocalThread线程去执行任务
    private final Executor executor;


    private final int maxPendingTasks;

    private final RejectedExecutionHandler rejectedExecutionHandler;






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
        rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
    }



    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    /**
     *
     */
    protected void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!offerTask(task)) {
            reject(task);
        }
    }

    final boolean offerTask(Runnable task) {
        if (isShutdown()) {
            reject();
        }
        boolean offer = taskQueue.offer(task);
        System.out.println(Thread.currentThread().getName() + " taskQueue 添加任务 offer:" + offer + " task = " + task);
        return offer;
    }

    protected final void reject(Runnable task) {
        rejectedExecutionHandler.rejected(task, this);
    }

    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
    }



    private void startThread() {
        if (state == ST_NOT_STARTED) {
            // cas 操作判断线程是否启动  并设置state=2
            if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
                try {
                    doStartThread();
                } catch (Throwable cause) {
                    STATE_UPDATER.set(this, ST_NOT_STARTED);
                    PlatformDependent.throwException(cause);
                }
            }
        }
    }

    /**
     * ThreadPerTaskExecutor创建一个线程nioEventLoop执行 taskQueue中的任务
     */
    private void doStartThread() {
        assert thread == null;

        // 创建一个任务 扔给NioEventLoop中的ThreadPerTaskExecutor（线程工程）
        Runnable r = new Thread("new thread runnable") {
            @Override
            public void run() {

                System.out.println(Thread.currentThread().getName() + " 刚创建一个thread 就执行我，并把这个线程赋值给thread属性");

                //把当前线程赋值给 NioEventLoop 从此NioEventLoop有了线程可以跑了
                thread = Thread.currentThread();

                if (interrupted) {
                    thread.interrupt();
                }

                boolean success = false;

                updateLastExecutionTime();
                try {
                    /**
                     *  内部类调用外部类的方法 语法就是这样的
                     *
                     *  执行 NioEventLoop.run()  调用外部类的run()
                     */
                    io.netty.util.concurrent.SingleThreadEventExecutor.this.run();
                    success = true;
                } catch (Throwable t) {
                    logger.warn("Unexpected exception from an event executor: ", t);
                } finally {

                    System.out.println(Thread.currentThread().getName() + " " + this + " 的thread 成功赋值  从此有了生命力, " +
                            "但是我永远不会执行到，因为上面的run() 是个死循环");

                    for (;;) {
                        int oldState = state;
                        if (oldState >= ST_SHUTTING_DOWN || STATE_UPDATER.compareAndSet(
                                io.netty.util.concurrent.SingleThreadEventExecutor.this, oldState, ST_SHUTTING_DOWN)) {
                            break;
                        }
                    }

                    // Check if confirmShutdown() was called at the end of the loop.
                    if (success && gracefulShutdownStartTime == 0) {
                        if (logger.isErrorEnabled()) {
                            logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " +
                                    io.netty.util.concurrent.SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must " +
                                    "be called before run() implementation terminates.");
                        }
                    }

                    try {
                        // Run all remaining tasks and shutdown hooks.
                        for (;;) {
                            if (confirmShutdown()) {
                                break;
                            }
                        }
                    } finally {
                        try {
                            cleanup();
                        } finally {
                            // Lets remove all FastThreadLocals for the Thread as we are about to terminate and notify
                            // the future. The user may block on the future and once it unblocks the JVM may terminate
                            // and start unloading classes.
                            // See https://github.com/netty/netty/issues/6596.
                            FastThreadLocal.removeAll();

                            STATE_UPDATER.set(io.netty.util.concurrent.SingleThreadEventExecutor.this, ST_TERMINATED);
                            threadLock.release();
                            if (!taskQueue.isEmpty()) {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("An event executor terminated with " +
                                            "non-empty task queue (" + taskQueue.size() + ')');
                                }
                            }
                            terminationFuture.setSuccess(null);
                        }
                    }
                }
                // ------ finally end ------
            }
        };

        // ThreadPerTaskExecutor 这里才会创建一个线程 并执行  doStartThread-task
        // 在执行任务的过程中 把当前线程 赋值给 NioEventLoop.thread  此时NioEventLoop才有活力
        executor.execute(r);
    }

    // --------------AbstractExecutorService jdk 方法实现-------------------------

    @Override
    public void execute(Runnable task) {
        // 这个方法很关键了  只要往这里放个任务 就开始给NioEventLoop绑定thread 并启动thread了
        // 把任务放到 taskQueue 队列中
        //command.run();
        if (task == null) {
            throw new NullPointerException("task");
        }

        boolean inEventLoop = inEventLoop();
        System.out.println(Thread.currentThread().getName() + " inEventLoop = " + inEventLoop);

        // 添加到 taskQueue 这个队列中   还有一个tailQueue队列
        addTask(task);

        if (!inEventLoop) {

            // 启动NioEventLoop中Thread   执行task 下一个方法
            startThread();

            if (isShutdown()) {
                boolean reject = false;
                try {
                    if (removeTask(task)) {
                        reject = true;
                    }
                } catch (UnsupportedOperationException e) {
                    // The task queue does not support removal so the best thing we can do is to just move on and
                    // hope we will be able to pick-up the task before its completely terminated.
                    // In worst case we will log on termination.
                }
                if (reject) {
                    reject();
                }
            }
        }

        if (!addTaskWakesUp && wakesUpForTask(task)) {
            wakeup(inEventLoop);
        }
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
