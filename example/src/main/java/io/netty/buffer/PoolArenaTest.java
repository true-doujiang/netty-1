package io.netty.buffer;

public class PoolArenaTest {


    public static void main(String[] args) {
//        ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;
//        // PooledUnsafeHeapByteBuf(ridx: 0, widx: 0, cap: 254)
//        ByteBuf byteBuf = alloc.heapBuffer(254);
//        byteBuf.writeInt(126);
//        System.out.println(byteBuf.readInt());

        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
//        int pageSize = 8192;
//        int maxOrder = 11;
//        int pageShifts = 8192;
//        int chunkSize = 16777216;
//        int directMemoryCacheAlignment = 0;
//        PoolArena.HeapArena heapArena = new PoolArena.HeapArena(allocator, pageSize, maxOrder, pageShifts, chunkSize, directMemoryCacheAlignment);

        PoolArena<byte[]> heapArena1 = allocator.heapArenas[0];
        PoolThreadCache cache = allocator.threadCache.get();

//        PoolArena directArena = null;
//        int tinyCacheSize = 512;
//        int smallCacheSize = 256;
//        int normalCacheSize = 64;
//        int DEFAULT_MAX_CACHED_BUFFER_CAPACITY = 32768;
//        int DEFAULT_CACHE_TRIM_INTERVAL = 8192;

//        PoolThreadCache cache = new PoolThreadCache(heapArena1, directArena,
//                tinyCacheSize, smallCacheSize, normalCacheSize,
//                DEFAULT_MAX_CACHED_BUFFER_CAPACITY, DEFAULT_CACHE_TRIM_INTERVAL);

        // PooledUnsafeHeapByteBuf
        ByteBuf buf = heapArena1.allocate(cache, 8192, Integer.MAX_VALUE);
        buf.writeInt(111);
        int a = buf.readInt();
        System.out.println("a = " + a);

        buf.release();

        ByteBuf buf2 = heapArena1.allocate(cache, 8192, Integer.MAX_VALUE);
        buf2.writeInt(111);
        int a2 = buf2.readInt();
        System.out.println("a2 = " + a2);

        //cache.allocateTiny(heapArena1, buf, 11, 16);
    }

}
