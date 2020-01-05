package com.yhh.example.ch3;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *  ChannelInitializer 也 extends ChannelInboundHandlerAdapter
 *  inBound
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 这些方法都是在 AbstractChannelHandlerContext 中调用的
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.pipeline().channel();
        System.out.println(Thread.currentThread().getName() + " =====用户自定义 ServerHandler handlerAdded()  ctx = " + ctx + " channel = " + channel);
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        Channel channel = ctx.pipeline().channel();
        System.out.println(Thread.currentThread().getName() + " =====用户自定义 ServerHandler channelRegistered() ctx = " + ctx + " channel = " + channel);
        // 我加上的  不然链条就断了 就不会到tail
        ctx.fireChannelRegistered();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(Thread.currentThread().getName() + "  =====用户自定义 ServerHandler channelActive ctx = " + ctx);
        ctx.fireChannelActive();

        Channel channel = ctx.pipeline().channel();
        System.out.println(Thread.currentThread().getName() + " =====用户自定义 ServerHandler channelActive() ctx = " + ctx + " channel = " + channel);
    }


    /**
     * AbstractChannelHandlerContext 中调用的
     * 服务端的 读到的 msg 是 NioSocketChannel
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(Thread.currentThread().getName() + " =====用户自定义 ServerHandler channelRead() ctx = " + ctx);

        // 一定要调用父类的方法  否则pipeline就到此结束 下个节点就不会执行了 ServerBootstrapAcceptor
        super.channelRead(ctx, msg);
        System.out.println(Thread.currentThread().getName() + " =====用户自定义 ServerHandler channelRead msg=" + msg);


        Channel channel = ctx.pipeline().channel();
        System.out.println(Thread.currentThread().getName() + " =====用户自定义 ServerHandler channelRead() ctx = " + ctx  +" channel = " + channel);

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
