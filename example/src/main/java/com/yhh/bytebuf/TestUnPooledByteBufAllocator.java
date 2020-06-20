package com.yhh.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class TestUnPooledByteBufAllocator {


    public static void main(String[] args) {
        f1();
        //f2();
    }

    public static void f1() {
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;

        ByteBuf byteBuf = alloc.heapBuffer(254);
        byteBuf.writeByte(100);

        byte b = byteBuf.readByte();
        System.out.println(b);

        byteBuf.release();
    }

    public static void f2() {
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;

        ByteBuf byteBuf = alloc.directBuffer(254);

        /**byte 范围   -128  -   127
         * 130  ->  -126
         * 129  ->  -127
         * 128  ->  -128
         * 127  ->  127
         */
        byteBuf.writeByte(130);

        System.out.println( byteBuf.readByte());

        byteBuf.release();
    }

}
