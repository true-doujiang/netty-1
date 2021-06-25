package io.netty.util;

import java.util.Arrays;



public class RecyclerStackTest {

    static class MyUser {

        private final Recycler.Handle<MyUser> handle;


        public MyUser(Recycler.Handle<MyUser> handle) {
            this.handle = handle;
        }

        public void recycle() {
            handle.recycle(this);
        }
    }

    private static final Recycler<MyUser> RECYCLER = new Recycler<MyUser>() {
        @Override
        protected MyUser newObject(Handle<MyUser> handle) {
            return new MyUser(handle);
        }
    };


    public static void main(String[] args) {
        Thread thread = Thread.currentThread();
        int maxCapacity = 5;
        int maxSharedCapacityFactor = 2;
        int ratioMask = 7;
        int maxDelayedQueues = 16;

        Recycler.Stack<MyUser> stack =
                new Recycler.Stack<MyUser>(RECYCLER, thread, maxCapacity, maxSharedCapacityFactor, ratioMask, maxDelayedQueues);

        MyUser myUser = pop(stack);
        System.out.println("myUser = " + myUser);
//        myUser.recycle();

        MyUser myUser2 = pop(stack);
        System.out.println("myUser2 = " + myUser2);
//         myUser2.recycle();

        MyUser myUser3 = pop(stack);
        System.out.println("myUser3 = " + myUser3);
//         myUser3.recycle();

        MyUser myUser4 = pop(stack);
        System.out.println("myUser4 = " + myUser4);
        //        myUser4.recycle();

        MyUser myUser5 = pop(stack);
        System.out.println("myUser5 = " + myUser5);
        //        myUser5.recycle();

        MyUser myUser6 = pop(stack);
        System.out.println("myUser6 = " + myUser6);

        myUser.recycle();
        myUser2.recycle();
        myUser3.recycle();
        myUser4.recycle();
        myUser5.recycle();
        myUser6.recycle();

        int size = stack.size;
        System.out.println("size = " + size);
        System.out.println("size = " + Arrays.toString(stack.elements));

        System.out.println("myUser6 = " + myUser6);

    }

    public static MyUser pop(Recycler.Stack<MyUser> stack) {
        // 这一段是在Recycler.get() 中copy出来的
        Recycler.DefaultHandle<MyUser> handle = stack.pop();
        if (handle == null) {
            // 创建一个Handle
            handle = stack.newHandle();
            //handle.value = newObject(handle);
            handle.value = RECYCLER.newObject(handle);
        }
        return (MyUser) handle.value;
    }
}
