package com.yhh.test2;

public class Main extends Person {

    public static void main(String[] args) {
        System.out.println();
        //Person p = new Person();
        Main m = new Main();
        new Thread(m).start();
    }

    @Override
    public void f() {
        System.out.println(this);
        super.f();
    }
}

abstract class Person implements Runnable {


    @Override
    public void run() {
        System.out.println(this);
        f();
    }

    public void f() {
        System.out.println(this);
    }
}


