package com.yhh.test5;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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

    /**
     * 时间戳跟时区不相关 就是从1970-1-1 00:00:00 到此时此刻的毫秒数  全世界都是一样的
     *
     * 时间是有时区的概念   同一个时间戳转换到不同的时区上对应的时间是不同的
     *
     * @throws Exception
     */
    @Test
    public void DateTest() throws Exception {
//        while (true) {
//            Date date = new Date();
//            // https://tool.lu/timestamp/ 打印的时间戳基本一致
//            System.out.println(date.getTime());
//            Thread.sleep(500);
//        }

        Date date = new Date(10000);
        //Thu Jan 01 08:00:10 CST 1970
        System.out.println(date);
        //10000
        System.out.println(date.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(date);
        //1970-01-01 08:00:10
        System.out.println(format);

        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String format2 = sdf.format(date);
        //1970-01-01 00:00:10
        System.out.println(format2);


    }

    @Test
    public void DateTest2() throws Exception {
//        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar instance = Calendar.getInstance();
        //instance.setTimeZone(TimeZone.getTimeZone("GMT"));
        instance.set(1970, 1, 0, 0, 0, 10);
        //Thread.sleep(2000);

        //TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        System.out.println("date="+date.getTime());

        Thread.sleep(1000);

        // 这个时间是执行 Calendar.getInstance()时生成的
        Date time = instance.getTime();
        System.out.println("time="+time.getTime());

        System.out.println(date.before(time));

        Date date1 = new Date(2020, 01, 14, 11, 27, 06);
        System.out.println(date1.getTime());
        boolean before = date.before(date1);
        System.out.println(before);
    }

}
