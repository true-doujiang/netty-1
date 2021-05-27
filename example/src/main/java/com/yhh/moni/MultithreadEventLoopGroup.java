package com.yhh.moni;

import com.yhh.moni.common.DefaultThreadFactory;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {



    // 2倍cpu核心数
    private static final int DEFAULT_EVENT_LOOP_THREADS;


    static {
        // 2倍cpu核心数
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
    }

    /**
     * 构造器
     * 调用方 NioEventLoopGroup#NioEventLoopGroup()
     */
    protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
        //super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
        // 2个NioEventLoop  不根据CPU核心计算了
        super(nThreads == 0 ? 1 : nThreads, executor, args);
    }




    protected ThreadFactory newDefaultThreadFactory() {
        Class<? extends MultithreadEventExecutorGroup> clazz = getClass();
        DefaultThreadFactory defaultThreadFactory = new DefaultThreadFactory(clazz, Thread.MAX_PRIORITY);
        return defaultThreadFactory;
    }

    /**
     * 直接调用父类的 next() 选择一个 NioEventLoop
     */
    @Override
    public EventLoop next() {
        //  chooser.next();
        return (EventLoop) super.next();
    }

}