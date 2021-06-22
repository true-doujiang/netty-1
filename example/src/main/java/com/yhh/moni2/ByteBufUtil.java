package com.yhh.moni2;



public class ByteBufUtil {


    // 静态static 根据实际情况初始化
    static final ByteBufAllocator DEFAULT_ALLOCATOR;

    static {
        ByteBufAllocator alloc;
        alloc = PooledByteBufAllocator.DEFAULT;

        System.out.println(Thread.currentThread().getName() + " ByteBufUtil 根据实际情况初始化: " + alloc);
        DEFAULT_ALLOCATOR = alloc;
    }


}
