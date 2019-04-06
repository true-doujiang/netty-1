package com.yhh.test;

public class Main1 {


    public static void main(String[] args) {
        final Dog dog = new Dog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dog.gg();
            }
        }).start();
    }
}
