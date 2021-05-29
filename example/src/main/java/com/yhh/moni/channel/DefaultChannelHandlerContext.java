package com.yhh.moni.channel;


import com.yhh.moni.EventExecutor;

/**
 * HandlerContext 唯一实现类
 */
final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {


    private final ChannelHandler handler;

    /**
     * default constructor
     */
    DefaultChannelHandlerContext(DefaultChannelPipeline pipeline,
                                 EventExecutor executor,
                                 String name,
                                 ChannelHandler handler) {

        super(pipeline, executor, name, isInbound(handler), isOutbound(handler));

        if (handler == null) {
            throw new NullPointerException("handler");
        }
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return handler;
    }

    // 判断是 in 还是 out
    private static boolean isInbound(ChannelHandler handler) {
        return handler instanceof ChannelInboundHandler;
    }

    private static boolean isOutbound(ChannelHandler handler) {
        return handler instanceof ChannelOutboundHandler;
    }
}
