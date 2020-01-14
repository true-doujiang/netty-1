package com.yhh.test5;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanTest {

    private static AtomicBoolean wakeUp = new AtomicBoolean();

    public static void main(String[] args) {
        // false
        System.out.println(wakeUp);

        /**
         * Atomically sets the value to the given updated value
         * if the current value {@code ==} the expected value.
         *
         * @param expect the expected value  当前值，写对了才能更新才能成功为 update
         * @param update the new value
         * @return {@code true} if successful. False return indicates that
         * the actual value was not equal to the expected value.
         */
        boolean b = wakeUp.compareAndSet(true, false);
        System.out.println(wakeUp);
        System.out.println(b);
    }
}
