package com.yhh.moni;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.internal.ObjectUtil;

import java.util.Queue;
import java.util.concurrent.Executor;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {


    private final Queue<Runnable> tailTasks;


    protected SingleThreadEventLoop(EventLoopGroup parent,
                                    Executor executor,
                                    boolean addTaskWakesUp,
                                    int maxPendingTasks,
                                    RejectedExecutionHandler rejectedExecutionHandler) {

        super(parent, executor, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);

        // 干什么用的 tailTasks: MpscUnboundedArrayQueue
        tailTasks = newTaskQueue(maxPendingTasks);
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup) super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }


    @Override
    protected boolean hasTasks() {
        return super.hasTasks() || !tailTasks.isEmpty();
    }


    @Override
    protected void afterRunningAllTasks() {
        runAllTasksFrom(tailTasks);
    }


    @Override
    public ChannelFuture register(Channel channel) {
        // this : NioEventLoop
//        DefaultChannelPromise channelPromise = new DefaultChannelPromise(channel, this);
        DefaultChannelPromise channelPromise = new DefaultChannelPromise(null, null);
        // 下一个方法
        return register(channelPromise);
    }

    /**
     * 原参数为 DefaultChannelPromise
     */
//    @Override
//    public ChannelFuture register(final Channel promise) {
//        ObjectUtil.checkNotNull(promise, "promise");
//        Channel channel = promise.channel();
//        Channel.Unsafe unsafe = channel.unsafe();
        // unsafe 是 AbstractChannel.AbstractUnsafe
//        unsafe.register(this, promise);
        //promise.channel().unsafe().register(this, promise);
//        return promise;
//        return null;
//    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return null;
    }
}
