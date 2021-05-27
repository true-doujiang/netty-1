package com.yhh.moni.common;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadPerTaskExecutor implements Executor {

    private final ThreadFactory threadFactory;



    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    /**
     * 创建一个线程 并启动
     * 每次给threadFactory一个command threadFactory就会创建一个线程去执行command 并启动
     * 而且这个线程复制到了NioEventLoop.thread上    NioEventLoop开始没有线程的
     * @param command
     */
    @Override
    public void execute(Runnable command) {
        Thread t = threadFactory.newThread(command);
        t.start();
    }

}