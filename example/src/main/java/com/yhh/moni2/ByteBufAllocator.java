package com.yhh.moni2;


import io.netty.buffer.ByteBuf;

public interface ByteBufAllocator {

    // 接口中的成员变量只有一种类型，public static final ,所以可以直接省去修饰符。
    public static final ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;


    /**
     * Allocate a heap {@link ByteBuf} with the given initial capacity.
     */
    ByteBuf heapBuffer(int initialCapacity);

    /**
     * Allocate a heap {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuf heapBuffer(int initialCapacity, int maxCapacity);

}
