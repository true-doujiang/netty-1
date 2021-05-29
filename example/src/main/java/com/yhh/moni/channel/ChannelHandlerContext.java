package com.yhh.moni.channel;

import com.yhh.moni.EventExecutor;

public interface ChannelHandlerContext extends ChannelInboundInvoker, ChannelOutboundInvoker {



    Channel channel();

    EventExecutor executor();

    String name();

    ChannelHandler handler();


    ChannelPipeline pipeline();


}
