package io.netty.util;

class MyUser {

    private final Recycler.Handle<MyUser> handle;


    public MyUser(Recycler.Handle<MyUser> handle) {
        this.handle = handle;
    }

    public void recycle() {
        handle.recycle(this);
    }
}

public class RecyclerStackTest {


    private static final Recycler<MyUser> RECYCLER = new Recycler<MyUser>() {
        @Override
        protected MyUser newObject(Handle<MyUser> handle) {
            return new MyUser(handle);
        }
    };


    public static void main(String[] args) {
        Thread thread = Thread.currentThread();
        int maxCapacity = 4096;
        int maxSharedCapacityFactor = 2;
        int ratioMask = 7;
        int maxDelayedQueues = 16;

        Recycler.Stack<MyUser> stack =
                new Recycler.Stack<MyUser>(RECYCLER, thread, maxCapacity, maxSharedCapacityFactor, ratioMask, maxDelayedQueues);

        Recycler.DefaultHandle<MyUser> handle = stack.pop();
        if (handle == null) {
            handle = stack.newHandle();
            //handle.value = newObject(handle);
            handle.value = RECYCLER.newObject(handle);

        }

        MyUser value = (MyUser) handle.value;
        System.out.println("value = " + value);

        handle.recycle(value);

        Recycler.DefaultHandle<MyUser> handle2 = stack.pop();
        MyUser value2 = (MyUser) handle2.value;

        System.out.println("(value2 == value) = " + (value2 == value));
    }
}
