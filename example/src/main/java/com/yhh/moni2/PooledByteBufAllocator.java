package com.yhh.moni2;


import io.netty.buffer.ByteBuf;

public class PooledByteBufAllocator extends AbstractByteBufAllocator {


    public static final PooledByteBufAllocator DEFAULT = new PooledByteBufAllocator(true);




    @SuppressWarnings("deprecation")
    public PooledByteBufAllocator(boolean preferDirect) {
        this(preferDirect, 8, 8, 8192, 11);
    }


    /**
     * @deprecated use
     */
    @Deprecated
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
        this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder,
                512, 256, 64);
    }

    /**
     * @deprecated use
     */
    @Deprecated
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder,
                                  int tinyCacheSize, int smallCacheSize, int normalCacheSize) {
        this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, tinyCacheSize, smallCacheSize,
                normalCacheSize, true, 0);
    }

    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena,
                                  int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize,
                                  int smallCacheSize, int normalCacheSize,
                                  boolean useCacheForAllThreads) {
        this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder,
                tinyCacheSize, smallCacheSize, normalCacheSize,
                useCacheForAllThreads, 0);
    }

    /**
     * 最终还是调用我
     */
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder,
                                  int tinyCacheSize, int smallCacheSize, int normalCacheSize,
                                  boolean useCacheForAllThreads, int directMemoryCacheAlignment) {
        super(preferDirect);

    }




    /**
     * 堆内存
     *
     * @param initialCapacity
     * @param maxCapacity
     * @return
     */
    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        // 从当前线程缓存中获取一块内存,会触发threadlocal的initValue()
//        PoolThreadCache cache = threadCache.get();
//        PoolArena<byte[]> heapArena = cache.heapArena;
//
//        final ByteBuf buf;
//        if (heapArena != null) {
//            // 内存规格化
//            buf = heapArena.allocate(cache, initialCapacity, maxCapacity);
//        } else {
//            buf = PlatformDependent.hasUnsafe() ?
//                    new UnpooledUnsafeHeapByteBuf(this, initialCapacity, maxCapacity) :
//                    new UnpooledHeapByteBuf(this, initialCapacity, maxCapacity);
//        }
//
//        return toLeakAwareBuffer(buf);


        return null;
    }

}
