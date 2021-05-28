package com.yhh.demo4;

import java.util.concurrent.atomic.AtomicInteger;

public class TestNioEventLoopThread {
    public static void main(String[] args) {
        NioEventLoop nioEventLoop = new NioEventLoop();
        nioEventLoop.execute();
        nioEventLoop.execute();
        nioEventLoop.execute();
        System.out.println("nioEventLoop = " + nioEventLoop);
    }
}

class NioEventLoop {

    public Thread thread;
    private int threadCounter = 1;

    public void execute() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("this = " + this);
                synchronized (NioEventLoop.class) {
                    thread = Thread.currentThread();
                    NioEventLoop.this.run();
                }
            }
        };

        System.out.println("threadCounter = " + threadCounter);
        //
        Thread thread = new Thread(runnable, "George" + threadCounter++);
        thread.start();
        int hashCode = thread.hashCode();
        System.out.println("hashCode = " + hashCode);
    }


    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(thread.hashCode() + " i = " + i );
        }
    }
}
