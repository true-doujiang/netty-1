package com.yhh.example.ch10;

import io.netty.util.Recycler;

public class MyUser {

    private final Recycler.Handle<MyUser> handle;


    public MyUser(Recycler.Handle<MyUser> handle) {
        this.handle = handle;
    }

    public void recycle() {
        handle.recycle(this);
    }
}