package io.netty.buffer;

public class PoolArenaTest {


    public static void main(String[] args) {
        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        int pageSize = 8192;
        int maxOrder = 11;
        int pageShifts = 8192;
        int chunkSize = 16777216;
        int directMemoryCacheAlignment = 0;
        PoolArena.HeapArena heapArena = new PoolArena.HeapArena(allocator, pageSize, maxOrder, pageShifts, chunkSize, directMemoryCacheAlignment);


        heapArena.allocate(cache, 11, 1024*1024*16);
    }

}
