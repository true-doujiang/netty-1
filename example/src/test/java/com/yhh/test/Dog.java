package com.yhh.test;

public class Dog extends Animal {

    private UnSafe unSafe = new UnSafe();

    public void gg() {
        System.out.println(this);
        unSafe.safeTest();
    }


    @Override
    public void f() {
        System.out.println(this);
        System.out.println("Dog f()");
    }
}
