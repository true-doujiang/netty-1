package com.yhh.moni2;

import io.netty.buffer.ByteBuf;

public abstract class AbstractByteBufAllocator implements ByteBufAllocator {


    // 默认初始化容量
    static final int DEFAULT_INITIAL_CAPACITY = 256;
    // 默认最大容量
    static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE;


    static {
        System.out.println("true = " + true);
    }


    //
    private final boolean directByDefault;
    // 如果要是要个 初始容量,最大容量都为0的buffer直接返回我就可以了
//    private final ByteBuf emptyBuf;

    protected AbstractByteBufAllocator(boolean preferDirect) {
        directByDefault = preferDirect;
//        emptyBuf = new EmptyByteBuf(this);
//        emptyBuf = new EmptyByteBuf(null);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity) {
        return heapBuffer(initialCapacity, DEFAULT_MAX_CAPACITY);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
        if (initialCapacity == 0 && maxCapacity == 0) {
            return null;
        }
        return newHeapBuffer(initialCapacity, maxCapacity);
    }



    /**
     * Create a heap {@link ByteBuf} with the given initialCapacity and maxCapacity.
     *
     * 有具体子类实现：
     *          PooledByteBufAllocator
     *          UnpooledByteBufAllocator
     */
    protected abstract ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity);

}
