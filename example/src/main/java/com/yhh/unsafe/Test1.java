package com.yhh.unsafe;

import io.netty.util.internal.ReflectionUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Test1 {


    public static void main(String[] args) {

        f1();
    }

    private static void f1() {

        final Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                    //Throwable cause = ReflectionUtil.trySetAccessible(unsafeField, false);

                    try {
                        unsafeField.setAccessible(true);
                    } catch (SecurityException e) {
                        return e;
                    } catch (RuntimeException e) {
                        return new RuntimeException(e);
                    }


                    return unsafeField.get(null);
                } catch (NoSuchFieldException e) {
                    return e;
                } catch (SecurityException e) {
                    return e;
                } catch (IllegalAccessException e) {
                    return e;
                } catch (NoClassDefFoundError e) {
                    return e;
                }
            }
        });

        System.out.println(maybeUnsafe);
    }


    public static void f2() {
        Integer integer = AccessController.doPrivileged(new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                int sum = 0;
                for (int i = 1; i <= 100; i++) {
                    sum += i;
                }
                return sum;
            }
        });
        System.out.println(integer);
    }
}
