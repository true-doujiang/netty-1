package com.yhh.moni;


import java.util.concurrent.*;

public class MyTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        final NioEventLoop loop = (NioEventLoop) group.next();

        EventExecutor next = group.next();
//        loop.shutdownNow();
//        System.out.println("next = " + next);


        loop.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " execute i = " + i);
                }
            }
        });
        loop.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " execute2 i = " + i);
                }
            }
        });

        Future<Object> submit = loop.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " submit i = " + i);
                }
                return "Callable";
            }
        });


        Object o = submit.get(10, TimeUnit.SECONDS);
        System.out.println("o = " + o);


    }
}
