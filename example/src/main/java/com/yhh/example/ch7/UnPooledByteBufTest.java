package com.yhh.example.ch7;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

public class UnPooledByteBufTest {

    public static void main(String[] args) {
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;
        // io.netty.buffer.UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeHeapByteBuf
        ByteBuf byteBuf = alloc.heapBuffer(254);

        byteBuf.writeInt(126);
        System.out.println(byteBuf.readInt());

        byteBuf.release();


        ByteBuf directBuffer = alloc.directBuffer(254);
    }
}
