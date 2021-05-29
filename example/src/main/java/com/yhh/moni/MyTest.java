package com.yhh.moni;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class MyTest {

    private static final AtomicIntegerFieldUpdater<MyTest> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(MyTest.class, "state");

    // NioEventLoop中的Thread 状态
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private volatile int state = ST_NOT_STARTED;

    public void test() {
        // 如果当前值==expect，则更新为update 并返回true
        boolean b = STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED);
        System.out.println("b = " + b);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

//        MyTest myTest = new MyTest();
//        myTest.test();
//        myTest.test();
//        myTest.test();

        EventLoopGroup group = new NioEventLoopGroup(1);
        final NioEventLoop loop = (NioEventLoop) group.next();

        EventExecutor next = group.next();
//        loop.shutdownNow();
//        System.out.println("next = " + next);

        Runnable task1 = new Thread("task1") {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " execute i = " + i);
                }
            }
        };
        System.out.println("task1 = " + task1);
        loop.execute(task1);

        Runnable task2 = new Thread("task2") {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " execute2 i = " + i);
                }
            }
        };
        System.out.println("task2 = " + task2);
        loop.execute(task2);

        Callable<Object> task3 = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " submit i = " + i);
                }
                return "Callable";
            }
        };
        System.out.println("task3 = " + task3);
        Future<Object> submit = loop.submit(task3);


        Object o = submit.get(10, TimeUnit.SECONDS);
        System.out.println("o = " + o);


    }
}
