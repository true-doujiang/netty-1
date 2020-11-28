package com.yhh.example.ch7;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class Scratch {


    public static void main(String[] args) {
        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        //PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 16)
        ByteBuf byteBuf = allocator.directBuffer(32);
        boolean b = byteBuf.release();
        System.out.println("b = " + b);
    }



}
