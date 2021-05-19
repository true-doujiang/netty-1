package com.yhh.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html
 *
 */
public class UnSafeUtil {


    public static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
