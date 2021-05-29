package com.yhh.moni.channel;

import com.yhh.moni.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext implements ChannelHandlerContext {



    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);


    volatile AbstractChannelHandlerContext next;
    volatile AbstractChannelHandlerContext prev;

    private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, "handlerState");

    private static final int ADD_PENDING = 1;
    private static final int ADD_COMPLETE = 2;
    private static final int REMOVE_COMPLETE = 3;
    private static final int INIT = 0;



    private final boolean inbound;
    private final boolean outbound;
    // handler的名字 pipeline.addLast() 一路传过来的
    private final String name;

    // 虽然 HandlerContext 上没有channel 但是可以通过pipeline 找到
    private final DefaultChannelPipeline pipeline;

    // Will be set to null if no child executor should be used, otherwise it will be set to the
    // child executor.
    final EventExecutor executor;

    private ChannelFuture succeededFuture;

    private volatile int handlerState = INIT;

    /**
     * 构造器
     * @param pipeline
     * @param executor new 的时候 set to null
     */
    AbstractChannelHandlerContext(DefaultChannelPipeline pipeline,
                                  EventExecutor executor,
                                  String name,
                                  boolean inbound,
                                  boolean outbound) {

        this.name = ObjectUtil.checkNotNull(name, "name");

        this.pipeline = pipeline;
        this.executor = executor;

        this.inbound = inbound;
        this.outbound = outbound;
    }


    @Override
    public Channel channel() {
        return pipeline.channel();
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    @Override
    public EventExecutor executor() {
        if (executor == null) {
            Channel channel = channel();
            return channel.eventLoop();
        } else {
            return executor;
        }
    }

    @Override
    public String name() {
        return name;
    }


    final boolean setAddComplete() {
        for (;;) {
            int oldState = handlerState;
            if (oldState == REMOVE_COMPLETE) {
                return false;
            }
            // Ensure we never update when the handlerState is REMOVE_COMPLETE already.
            // oldState is usually ADD_PENDING but can also be REMOVE_COMPLETE when an EventExecutor is used that is not
            // exposing ordering guarantees.
            if (HANDLER_STATE_UPDATER.compareAndSet(this, oldState, ADD_COMPLETE)) {
                return true;
            }
        }
    }
}
