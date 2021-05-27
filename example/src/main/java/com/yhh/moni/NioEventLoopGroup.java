package com.yhh.moni;

import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;

import java.util.concurrent.Executor;

public class NioEventLoopGroup extends MultithreadEventLoopGroup {


    public NioEventLoopGroup(int nThreads) {
        this(nThreads, (Executor) null);
    }


    /**
     * 构造器
     */
    public NioEventLoopGroup(int nThreads, Executor executor) {
        super(nThreads, executor, RejectedExecutionHandlers.reject());
    }



    @Override
    protected EventExecutor newChild(Executor executor, Object... args) throws Exception {
        // executor: ThreadPerTaskExecutor
        // this 启动的时候创建的 NioEventLoopGroup
        NioEventLoop nioEventLoop = new NioEventLoop(this, executor, (RejectedExecutionHandler) args[0]);
        return nioEventLoop;
    }
}
