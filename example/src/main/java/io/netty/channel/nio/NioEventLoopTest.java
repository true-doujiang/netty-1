package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class NioEventLoopTest {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        final NioEventLoop loop = (NioEventLoop) group.next();
        try {

            EventLoop next = loop.next();
            // loop.run();
            EventLoopGroup parent = loop.parent();

            Runnable task1 = new Thread("task1") {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " task1 execute i = " + i);
                    }
                }
            };

            loop.execute(task1);

            Runnable task2 = new Thread("task2") {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " task2 execute i = " + i);
                    }
                }
            };

            loop.execute(task2);

            Future<Integer[]> future = loop.submit(new Callable<Integer[]>() {
                @Override
                public Integer[] call() throws Exception {
                    Integer[] arr = new Integer[5];
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " submit i = " + i);
                        arr[i] = i;
                    }
                    return arr;
                }
            });
            // 要等任务执行完成了才能拿到结果
            Integer[] now = future.getNow();
            System.out.println("now = " + Arrays.toString(now));


            Channel channel = new NioServerSocketChannel();
            loop.register(channel).syncUninterruptibly();

            Selector selector = loop.unwrappedSelector();
            Selector selector1 = ((NioEventLoop) channel.eventLoop()).unwrappedSelector();
            boolean open = selector.isOpen();

            // Submit to the EventLoop so we are sure its really executed in a non-async manner.
            loop.submit(new Runnable() {
                @Override
                public void run() {
                    loop.rebuildSelector();
                }
            }).syncUninterruptibly();

            Selector newSelector = ((NioEventLoop) channel.eventLoop()).unwrappedSelector();

            channel.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }
}
