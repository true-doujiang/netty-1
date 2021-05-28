package com.yhh.moni;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.UnstableApi;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class SingleThreadEventExecutor extends AbstractEventExecutor {


    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);

    // NioEventLoop中的Thread 状态
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;

    private static final Runnable WAKEUP_TASK = new Runnable() {
        @Override
        public void run() {
            // Do nothing.
        }
    };

    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");


    private long lastExecutionTime;
    private volatile int state = ST_NOT_STARTED;

    private final Queue<Runnable> taskQueue;

    // ThreadPerTaskExecutor 创建的线程 就赋值给我了 我就是NioEventLoop中的线程，引擎
    private volatile Thread thread;
    // ThreadPerTaskExecutor: 给我一个任务我就新创建一个FastThreadLocalThread线程去执行任务
    private final Executor executor;

    private volatile boolean interrupted;


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
     * @see Queue#isEmpty() 子类覆盖了
     */
    protected boolean hasTasks() {
        assert inEventLoop();
        return !taskQueue.isEmpty();
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

    private boolean fetchFromScheduledTaskQueue() {
//        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        // 从优先级队列中获取，  模拟忽略
//        Runnable scheduledTask  = pollScheduledTask(nanoTime);
//        while (scheduledTask != null) {
//            //如果添加到普通任务队列过程中失败
//            if (!taskQueue.offer(scheduledTask)) {
//                //则重新添加到定时任务队列中
//                // No space left in the task queue add it back to the scheduledTaskQueue so we pick it up again.
//                scheduledTaskQueue().add((ScheduledFutureTask<?>) scheduledTask);
//                return false;
//            }
//            //继续从定时任务队列中拉取任务
//            //方法执行完成之后, 所有符合运行条件的定时任务队列, 都添加到了普通任务队列中
//            scheduledTask  = pollScheduledTask(nanoTime);
//        }
        return true;
    }

    /**
     * 取出 NioEventLoop taskQueue 中的任务执行
     */
    protected boolean runAllTasks() {
        assert inEventLoop();
        boolean fetchedAll;
        boolean ranAtLeastOne = false;

        do {
            fetchedAll = fetchFromScheduledTaskQueue();
            if (runAllTasksFrom(taskQueue)) {
                ranAtLeastOne = true;
            }
        } while (!fetchedAll); // keep on processing until we fetched all scheduled tasks.

//        if (ranAtLeastOne) {
//            lastExecutionTime = ScheduledFutureTask.nanoTime();
//        }

        // 绕到子类 把子类的tailTasks 传到 runAllTasksFrom()
        afterRunningAllTasks();
        return ranAtLeastOne;
    }

    protected final boolean runAllTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = pollTaskFrom(taskQueue);

        if (task == null) {
            // 这里 null 返回false
            return false;
        }

        for (;;) {
            System.out.println(Thread.currentThread().getName() + "  NioEventLoop.run() runAllTasksFrom(taskQueue) 取出任务执行   task: " + task);
            //  AbstractEventExecutor 中的静态方法
            safeExecute(task);
            task = pollTaskFrom(taskQueue);
            if (task == null) {
                // 这里 null 就返回true 了呢
                return true;
            }
        }
    }

    protected Runnable pollTask() {
        assert inEventLoop();
        return pollTaskFrom(taskQueue);
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        for (;;) {
            Runnable task = taskQueue.poll();
            if (task == WAKEUP_TASK) {
                continue;
            }
            return task;
        }
    }

    /**
     * 取出 NioEventLoop taskQueue 中的任务执行
     */
    protected boolean runAllTasks(long timeoutNanos) {
        //定时任务队列中聚合任务
        fetchFromScheduledTaskQueue();

        Runnable task = pollTask();

        if (task == null) {
            afterRunningAllTasks();
            return false;
        }

//        final long deadline = ScheduledFutureTask.nanoTime() + timeoutNanos;
        long runTasks = 0;
        long lastExecutionTime;

        // 一直for 从taskQueue中取任务执行，直到执行完毕或者超时
        for (;;) {
            System.out.println(Thread.currentThread().getName() + " NioEventLoop.run() runAllTasks(timeoutNanos) 取出任务执行 task: " + task);
            // 执行任务  task.run() //  AbstractEventExecutor 中的静态方法
            safeExecute(task);
            runTasks ++;



            task = pollTask();
            if (task == null) {
//                lastExecutionTime = ScheduledFutureTask.nanoTime();
                break;
            }
        }

        afterRunningAllTasks();
//        this.lastExecutionTime = lastExecutionTime;
        return true;
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
                // 其实thread就是 执行r的线程
                thread = Thread.currentThread();

                if (interrupted) {
                    thread.interrupt();
                }

                boolean success = false;

                updateLastExecutionTime();
                try {
                    /**
                     *  执行 NioEventLoop.run()  调用外部类的run()
                     */
                    SingleThreadEventExecutor.this.run();
                    success = true;
                } catch (Throwable t) {
                    logger.warn("Unexpected exception from an event executor: ", t);
                } finally {
                    System.out.println(Thread.currentThread().getName() + " " +
                            this + " 的thread 成功赋值  从此有了生命力, " +
                            "但是我永远不会执行到，因为上面的run() 是个死循环");
                    for (;;) {
                        int oldState = state;
                        if (oldState >= ST_SHUTTING_DOWN || STATE_UPDATER.compareAndSet(
                                SingleThreadEventExecutor.this, oldState, ST_SHUTTING_DOWN)) {
                            break;
                        }
                        System.out.println("oldState = " + oldState);
                    }
                }
                // ------ finally end ------
            }
        };

        // ThreadPerTaskExecutor 这里才会创建一个线程 并执行  doStartThread-task
        // 在执行任务的过程中 把当前线程 赋值给 NioEventLoop.thread  此时NioEventLoop才有活力
        executor.execute(r);
    }

    protected void updateLastExecutionTime() {
        //lastExecutionTime = ScheduledFutureTask.nanoTime();
    }


    @UnstableApi
    protected void afterRunningAllTasks() { }


    /**
     * NioEventLoop 的run
     */
    protected abstract void run();





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

            // 启动NioEventLoop中Thread   执行task 上一个方法
            startThread();

//            if (isShutdown()) {
//                boolean reject = false;
//                try {
//                    if (removeTask(task)) {
//                        reject = true;
//                    }
//                } catch (UnsupportedOperationException e) {
//
//                }
//                if (reject) {
//                    reject();
//                }
//            }
        }

//        if (!addTaskWakesUp && wakesUpForTask(task)) {
//            wakeup(inEventLoop);
//        }
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
