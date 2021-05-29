package com.yhh.moni;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

public interface EventLoopGroup extends EventExecutorGroup {

//
    @Override
    EventLoop next();


    //MultithreadEventLoopGroup、 SingleThreadEventLoop 实现了这2个方法
    ChannelFuture register(Channel channel);

    ChannelFuture register(ChannelPromise promise);
}