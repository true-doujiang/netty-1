package com.yhh.moni;


import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Executor;

public class NioEventLoop extends SingleThreadEventLoop {


    protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16,
            SystemPropertyUtil.getInt("io.netty.eventLoop.maxPendingTasks", Integer.MAX_VALUE));



    /**
     * 构造器
     * 调用方: io.netty.channel.nio.NioEventLoopGroup#newChild()
     */
    NioEventLoop(NioEventLoopGroup parent, Executor executor, RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
    }

    @Override
    protected void run() {
        System.out.println(Thread.currentThread().getName()
                + " " + this + " NioEventLoop.run() 1 start 从此进入死循环 ");
        for (;;) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " time = " + new Date().getSeconds() );

        }// for end
    }


    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        // This event loop never calls takeTask()
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.<Runnable>newMpscQueue()
                : PlatformDependent.<Runnable>newMpscQueue(maxPendingTasks);
    }



}
