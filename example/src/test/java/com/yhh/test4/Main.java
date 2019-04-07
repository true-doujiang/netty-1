package com.yhh.test4;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class Main {


    public static void main(String[] args) {
        Dog dog = new Dog();
        dog.execute();
    }

}

class Dog implements Runnable {

    private Thread thread;
    private Executor executor = new ThreadPerTaskExecutor(new DefaultThreadFactory());

    public void execute() {
        System.out.println("Dog " + this);
        executor.execute(new Runnable() {
            //相当于内部类，引用外部类的方法
            @Override
            public void run() {
                thread = Thread.currentThread();
                System.out.println("thread  = " + thread);
                Dog.this.run();
            }
        });
    }

    @Override
    public void run() {
        System.out.println("Dog " + this);
    }
}



class Cat implements Runnable {

    private Thread thread;
    private Executor executor = new ThreadPerTaskExecutor(new DefaultThreadFactory());

    public void execute() {
        System.out.println("Cat " + this);
    }

    @Override
    public void run() {
        System.out.println("Cat " + this);
    }
}


class ThreadPerTaskExecutor implements Executor {

    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}

class DefaultThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        System.out.println("DefaultThreadFactory 新创建Thread：" + t);
        return t;
    }
}
