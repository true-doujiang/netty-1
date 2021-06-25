package com.yhh.example.ch10;

import io.netty.util.Recycler;


/**
 * https://www.cnblogs.com/jackion5/p/11369705.html
 */
public class RecycleTest {

    private static final Recycler<MyUser> RECYCLER = new Recycler<MyUser>() {
        @Override
        protected MyUser newObject(Handle<MyUser> handle) {
            return new MyUser(handle);
        }
    };


    public static void main(String[] args) {
        MyUser user = RECYCLER.get();
        MyUser user2 = RECYCLER.get();
        MyUser user3 = RECYCLER.get();
        MyUser user4 = RECYCLER.get();
        MyUser user5 = RECYCLER.get();

        user.recycle();
        // todo 只有第一个对象入栈了，其它都没有入栈呢  ？？？？？？
        user2.recycle();
        user3.recycle();
        user4.recycle();
        user5.recycle();

        MyUser myUser = RECYCLER.get();

        MyUser user1 = RECYCLER.get();

        System.out.println(user1 == user);
    }
}
