package com.yhh.moni.channel;


import com.yhh.moni.EventLoop;

public interface Channel {


    EventLoop eventLoop();

    Channel parent();


    ChannelPipeline pipeline();


    Unsafe unsafe();

    interface Unsafe {

    }

}
