package com.yhh.example.ch10;

/**
 * @author -小野猪-
 * @version 1.0
 * @date 2020-06-25 23:27
 * @desc todo
 **/
public class TestThreadLocalMap {

    static class User {
        public String name;
    }

    public static void main(String[] args) {
        User u1 = new User();
        u1.name = "用户1";
        ThreadLocal threadLocal1 = new ThreadLocal(){
            @Override
            protected Object initialValue() {
                return u1;
            }
        };
        System.out.println("threadLocal1.get() = " + threadLocal1.get());

        User u2 = new User();
        u2.name = "用户2";
        ThreadLocal threadLocal2 = new ThreadLocal(){
            @Override
            protected Object initialValue() {
                return u2;
            }
        };
        System.out.println("threadLocal2.get() = " + threadLocal2.get());

        User u3 = new User();
        u3.name = "用户3";
        ThreadLocal threadLocal3 = new ThreadLocal(){
            @Override
            protected Object initialValue() {
                return u3;
            }
        };
        System.out.println("threadLocal3.get() = " + threadLocal3.get());
        System.out.println("threadLocal3.get() = " + threadLocal3.get());

        threadLocal1 = null;
        threadLocal2 = null;
        /**
         * gc后 threadLocal1，threadLocal1 被回收 ThreadLocal.ThreadLocalMap中这2个key变为null
         * 但是对应的value u1，u2会依然存在导致内存泄露
         */
        System.gc();
        /**
         * 三个ThreadLocal属于同一个线程  所以用的是同一个 ThreadLocal.ThreadLocalMap
         */
        Object o = threadLocal3.get();
        System.out.println(o);

        System.out.println(threadLocal3);

    }

}


