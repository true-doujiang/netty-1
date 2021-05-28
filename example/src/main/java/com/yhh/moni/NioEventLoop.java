package com.yhh.moni;


import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class NioEventLoop extends SingleThreadEventLoop {


    // 应该是定义在 SingleThreadEventLoop 中的
    protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16,
            SystemPropertyUtil.getInt("io.netty.eventLoop.maxPendingTasks", Integer.MAX_VALUE));


    private final AtomicBoolean wakenUp = new AtomicBoolean();

    private volatile int ioRatio = 50;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    /**
     * 构造器
     * 调用方: io.netty.channel.nio.NioEventLoopGroup#newChild()
     */
    NioEventLoop(NioEventLoopGroup parent, Executor executor, RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
    }

    @Override
    protected void run() {
        System.out.println(Thread.currentThread().getName() + " " + this + " NioEventLoop.run() 1 start 从此进入死循环 ");
        for (;;) {
            boolean b = hasTasks();
            System.out.println(Thread.currentThread().getName() +  " b = " + b);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " time = " + new Date().getSeconds() );

            cancelledKeys = 0;
            needsToSelectAgain = false;
            final int ioRatio = this.ioRatio;
            //System.out.println("ioRatio = " + ioRatio);
            if (ioRatio == 100) {
//                try {
//                    processSelectedKeys();
//                } finally {
//                    // Ensure we always run tasks.  父类.父类中
//                    runAllTasks();
//                }
                runAllTasks();
            } else {
                final long ioStartTime = System.nanoTime();
//                try {
//                    // 这个负责已经经接入channel的 IO 处理
//                    processSelectedKeys();
//                } finally {
//                    // Ensure we always run tasks.
//                    final long ioTime = System.nanoTime() - ioStartTime;
//                    // 这个新接入channel 任务处理  TODO ??
//                    runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
//                }
                final long ioTime = System.nanoTime() - ioStartTime;
                // 这个新接入channel 任务处理  TODO ??
                runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
            }


        }// for end
    }


    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        // This event loop never calls takeTask()
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.<Runnable>newMpscQueue()
                : PlatformDependent.<Runnable>newMpscQueue(maxPendingTasks);
    }

    @Override
    protected Runnable pollTask() {
        Runnable task = super.pollTask();
        if (needsToSelectAgain) {
//            selectAgain();
        }
        return task;
    }

}
