package com.yhh.test3;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {

//    public static void main(String[] args) {
//
//    }

    //    public static
    @Test
    public void f() {
        AtomicInteger idx = new AtomicInteger();
        int len = 999999999;
        long s = System.currentTimeMillis();

        for (int i = 0; i < len; i++) {
            int abs = Math.abs(idx.getAndIncrement() & len - 1);
            //System.out.println(abs);
        }

        System.out.println("f" + (System.currentTimeMillis() - s));
    }

    @Test
    public void g() {
        AtomicInteger idx = new AtomicInteger();
        int len = 999999999;
        long s = System.currentTimeMillis();

        for (int i = 0; i < len; i++) {
            int abs = Math.abs(idx.getAndIncrement() % len);
            //System.out.println(abs);
        }

        System.out.println("g" + (System.currentTimeMillis() - s));
    }

    @Test
    public void test1() {
        System.out.println(Math.abs(-1));
    }
}
