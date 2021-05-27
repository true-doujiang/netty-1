package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.Selector;

public class NioEventLoopTest1 {


    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        final NioEventLoop nioEventLoop = (NioEventLoop) group.next();
        try {

            EventLoop next = nioEventLoop.next();
            // loop.run();
            EventLoopGroup parent = nioEventLoop.parent();

            nioEventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " execute i = " + i);
                    }
                }
            });
            nioEventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " execute2 i = " + i);
                    }
                }
            });

            nioEventLoop.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " submit i = " + i);
                    }
                }
            });



            Channel channel = new NioServerSocketChannel();
            nioEventLoop.register(channel).syncUninterruptibly();

            Selector selector = nioEventLoop.unwrappedSelector();
            Selector selector1 = ((NioEventLoop) channel.eventLoop()).unwrappedSelector();


            // Submit to the EventLoop so we are sure its really executed in a non-async manner.
            nioEventLoop.submit(new Runnable() {
                @Override
                public void run() {
                    nioEventLoop.rebuildSelector();
                }
            }).syncUninterruptibly();

//            Selector newSelector = ((NioEventLoop) channel.eventLoop()).unwrappedSelector();
//            assertTrue(newSelector.isOpen());
//            assertNotSame(selector, newSelector);
//            assertFalse(selector.isOpen());

            channel.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }
}
