/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Abstract base class for {@link EventLoopGroup} implementations that handles their tasks with multiple threads at
 * the same time.
 */
//                    抽象类
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MultithreadEventLoopGroup.class);

    // 2倍cpu核心数
    private static final int DEFAULT_EVENT_LOOP_THREADS;


    static {
        // 2倍cpu核心数
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.eventLoopThreads: {}", DEFAULT_EVENT_LOOP_THREADS);
        }
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

    /**
     * @see MultithreadEventExecutorGroup#MultithreadEventExecutorGroup(int, ThreadFactory, Object...)
     */
    protected MultithreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, threadFactory, args);
    }

    /**
     * @see MultithreadEventExecutorGroup#MultithreadEventExecutorGroup(int, Executor,
     * EventExecutorChooserFactory, Object...)
     */
    protected MultithreadEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory,
                                     Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, chooserFactory, args);
    }



    /**
     * 获取默认的线程工厂并且传入当前类名
     * MultithreadEventExecutorGroup中定义的，它的实现被这里覆盖了
     */
    @Override
    protected ThreadFactory newDefaultThreadFactory() {
        Class<? extends MultithreadEventLoopGroup> clazz = getClass();
        DefaultThreadFactory defaultThreadFactory = new DefaultThreadFactory(clazz, Thread.MAX_PRIORITY);
        return defaultThreadFactory;
    }

    /**
     * 直接调用父类的 next() 选择一个 NioEventLoop
     * EventExecutorGroup 中定义
     */
    @Override
    public EventLoop next() {
        //  chooser.next();
        return (EventLoop) super.next();
    }

    /**
     * 父类中定义的  我这里只是再重新声明一下， 注释掉也可以的
     * NioEventLoopGroup 具体实现
     */
    @Override
    protected abstract EventLoop newChild(Executor executor, Object... args) throws Exception;

    /**
     *  EventLoopGroup 定义
     *  EventLoop的实现类也实现了register()
     */
    @Override
    public ChannelFuture register(Channel channel) {
        EventLoop next = next();
        return next.register(channel);
    }

    // EventLoopGroup 定义
    // EventLoop的实现类也实现了register()
    @Override
    public ChannelFuture register(ChannelPromise promise) {
        EventLoop next = next();
        return next.register(promise);
    }

    @Deprecated
    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        EventLoop next = next();
        return next.register(channel, promise);
    }
}
