package com.yhh.example.ch3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler2 extends ChannelInboundHandlerAdapter {

    /**
     * 这些方法都是在 AbstractChannelHandlerContext 中调用的
     */

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(Thread.currentThread().getName() + " channelActive2");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println(Thread.currentThread().getName() + " channelRegistered2");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println(Thread.currentThread().getName() + " handlerAdded2");
    }

    /**
     * AbstractChannelHandlerContext 中调用的
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println(Thread.currentThread().getName() + " channelRead2");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 耗时的操作
//                String result = loadFromDB();
//
//                ctx.channel().writeAndFlush(result);
//                ctx.executor().schedule(new Runnable() {
//                    @Override
//                    public void run() {
//                        // ...
//                    }
//                }, 1, TimeUnit.SECONDS);
//
//            }
//        }).start();
    }

    private String loadFromDB() {
        return "hello world!";
    }
}
