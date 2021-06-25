package com.yhh.example.ch10;

import io.netty.util.Recycler;



public class RecycleTest {

    private static final Recycler<MyUser> RECYCLER = new Recycler<MyUser>() {
        @Override
        protected MyUser newObject(Handle<MyUser> handle) {
            return new MyUser(handle);
        }
    };


    public static void main(String[] args) {
        MyUser user = RECYCLER.get();

        user.recycle();
        RECYCLER.get().recycle();

        MyUser user1 = RECYCLER.get();

        System.out.println(user1 == user);
    }
}
