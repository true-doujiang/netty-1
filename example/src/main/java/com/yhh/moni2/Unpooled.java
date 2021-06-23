package com.yhh.moni2;

import io.netty.buffer.ByteBuf;

public final class Unpooled {


    private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;


    public static ByteBuf buffer(int initialCapacity) {
        ByteBuf byteBuf = ALLOC.heapBuffer(initialCapacity);

        ByteBufAllocator allocator = ALLOC.DEFAULT;
        System.out.println("allocator = " + allocator);

        return byteBuf;
    }
}
