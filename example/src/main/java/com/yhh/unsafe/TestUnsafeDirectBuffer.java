package com.yhh.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TestUnsafeDirectBuffer {

    static Unsafe UNSAFE = UnSafeUtil.getUnsafe();

    public static void main(String[] args) throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(16);
        Field field = Buffer.class.getDeclaredField("address");
        long objectFieldOffset = UNSAFE.objectFieldOffset(field);

        long address = UNSAFE.getLong(byteBuffer, objectFieldOffset);
        System.out.println("address = " + address);

        UNSAFE.putInt(address, 190);
        UNSAFE.putByte(address + 4, (byte)-10);

        //默认是写模式，需要转换到读模式
        //byteBuffer.flip();
        //读出来的肯定不对，有高低位转换
        //int anInt = byteBuffer.getInt();
        //byte b = byteBuffer.get();
        //System.out.println("b = " + b);

        int anInt1 = UNSAFE.getInt(address);
        byte aByte = UNSAFE.getByte(address + 4);
        System.out.println("aByte = " + aByte);


    }
}
