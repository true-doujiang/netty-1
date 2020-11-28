package com.yhh.example.ch7;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

public class PooledByteBufTest {

    public static void main(String[] args) {
        ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;
        // PooledUnsafeHeapByteBuf(ridx: 0, widx: 0, cap: 254)
        ByteBuf byteBuf = alloc.heapBuffer(254);

        byteBuf.writeInt(126);
        System.out.println(byteBuf.readInt());

        byteBuf.release();
    }
}
