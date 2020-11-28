package com.yhh.example.ch7;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class UnSafeTest {

    //https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html 以及本博客的参考资料

    public static void main(String[] args) {
        // 底层就是用的unsafe
//        ByteBuffer nioDirectBuf = ByteBuffer.allocateDirect(10);
//        System.out.println("nioDirectBuf = " + nioDirectBuf);
//        nioDirectBuf.putInt(100);
//        nioDirectBuf.flip();
//        //底层用到了位移操作  Integer.reverseBytes(100);
//        int anInt = nioDirectBuf.getInt();
//        System.out.println("anInt = " + anInt);

        Unsafe unsafe = reflectGetUnsafe();
        System.out.println("unsafe = " + unsafe);
        //分配内存,才能获取到内存地址
        long baseAddress = unsafe.allocateMemory(8);
        int anInt1 = unsafe.getInt(baseAddress);
        int anInt2 = unsafe.getInt(baseAddress);
        //内存初始化
        unsafe.setMemory(baseAddress, 8, (byte)0);
        int anInt3 = unsafe.getInt(baseAddress);
        int anInt4 = unsafe.getInt(baseAddress);

        unsafe.putInt(baseAddress, 101);
        unsafe.putInt(baseAddress+4, 99);

        int getValue = unsafe.getInt(baseAddress);
        int anInt = unsafe.getInt(baseAddress+4);
        int anInt5 = unsafe.getInt(baseAddress + 8);


        System.out.println("getValue = " + getValue);


    }

    private static Unsafe reflectGetUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            e.getCause();
            return null;
        }

    }
}