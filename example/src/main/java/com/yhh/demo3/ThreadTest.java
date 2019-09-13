package com.yhh.demo3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        new ThreadTest().f();
    }

    public void f() {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                ThreadTest.this.run();
            }
        };

        executorService.submit(task);
    }

    public void run() {
        for (int i = 0; i< 10; i++) {
            System.out.println(Thread.currentThread().getName() + "====" + i);
        }
    }
}


