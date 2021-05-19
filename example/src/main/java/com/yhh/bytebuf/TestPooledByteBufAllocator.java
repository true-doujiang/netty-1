package com.yhh.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

public class TestPooledByteBufAllocator {

    public static void main(String[] args) {
        //test1();
        test2();
    }

    public static void test1() {
        ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;
        // PooledUnsafeHeapByteBuf(ridx: 0, widx: 0, cap: 254)
        ByteBuf byteBuf = alloc.heapBuffer(254);

        byteBuf.writeInt(126);
        System.out.println(byteBuf.readInt());

        byteBuf.release();
    }


    public static void test2() {

        // PooledByteBufAllocator(true)
        ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

        //tiny规格内存分配 会变成大于等于16的整数倍的数：这里254 会规格化为256
        ByteBuf byteBuf = alloc.directBuffer(1024 * 8 * 2);

        //读写bytebuf
        byteBuf.writeInt(126);
        System.out.println(byteBuf.readInt());

        //很重要，内存释放
        byteBuf.release();
    }
}
