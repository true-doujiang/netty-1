package com.yhh.test;

public abstract class Animal {



    public abstract void f();

    public class UnSafe {
        public void safeTest() {
            System.out.println(this);
            f();
        }
    }
}
