package io.netty.buffer;

import java.util.ArrayList;
import java.util.List;

public class PoolArenaTest {


    public static void main(String[] args) throws Exception {
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

        PoolArena<byte[]> minArena = allocator.heapArenas[0];

        for (int i = 1; i < allocator.heapArenas.length; i++) {
            PoolArena<byte[]> arena = allocator.heapArenas[i];
            if (arena.numThreadCaches.get() < minArena.numThreadCaches.get()) {
                minArena = arena;
            }
        }
        //PoolArena<byte[]> heapArena1 = allocator.heapArenas[0];

        PoolThreadCache cache = allocator.threadLocalCache.get();

//        PoolArena directArena = null;
//        int tinyCacheSize = 512;
//        int smallCacheSize = 256;
//        int normalCacheSize = 64;
//        int DEFAULT_MAX_CACHED_BUFFER_CAPACITY = 32768;
//        int DEFAULT_CACHE_TRIM_INTERVAL = 8192;

//        PoolThreadCache cache = new PoolThreadCache(heapArena1, directArena,
//                tinyCacheSize, smallCacheSize, normalCacheSize,
//                DEFAULT_MAX_CACHED_BUFFER_CAPACITY, DEFAULT_CACHE_TRIM_INTERVAL);

        List<ByteBuf> list = new ArrayList();
        for (int i = 1; i <=10; i++) {
            // PooledUnsafeHeapByteBuf
            ByteBuf buf = minArena.allocate(cache, 8192*i*10*10, Integer.MAX_VALUE);
            buf.writeInt(i);
            int a = buf.readInt();
            System.out.println(i + "a = " + a);
            list.add(buf);

            PooledByteBuf pooledByteBuf = (PooledByteBuf) buf;
            System.out.println(pooledByteBuf.chunk
                    + "  handdle:" + pooledByteBuf.handle
                    + "  offset:" + pooledByteBuf.offset
                    + "  length:" + pooledByteBuf.length
                    + "  maxLength:" + pooledByteBuf.maxLength);
            //buf.release();
        }
        System.out.println("list = " + list);

        ByteBuf buf2 = minArena.allocate(cache, 8192+1, Integer.MAX_VALUE);
        buf2.writeInt(222);
        int a2 = buf2.readInt();
        System.out.println("a2 = " + a2);

        PooledByteBuf pooledByteBuf2 = (PooledByteBuf) buf2;

        System.out.println(pooledByteBuf2.chunk
                + "  handdle:" + pooledByteBuf2.handle
                + "  offset:" + pooledByteBuf2.offset
                + "  length:" + pooledByteBuf2.length
                + "  maxLength:" + pooledByteBuf2.maxLength);

        ByteBuf buf3 = minArena.allocate(cache, 8192*1024, Integer.MAX_VALUE);
        buf3.writeInt(333);
        int a3 = buf3.readInt();
        System.out.println("a3 = " + a3);

        PooledByteBuf pooledByteBuf3 = (PooledByteBuf) buf3;
        System.out.println(pooledByteBuf3.chunk
                + "  handdle:" + pooledByteBuf3.handle
                + "  offset:" + pooledByteBuf3.offset
                + "  length:" + pooledByteBuf3.length
                + "  maxLength:" + pooledByteBuf3.maxLength);
    }

}


//        byte[] memory = (byte[]) ((PooledByteBuf) buf2).memory;
//        OutputStream out = new FileOutputStream("D:\\log/ccc.txt");
//
//        String s = Arrays.toString(memory);
//        InputStream is = new ByteArrayInputStream(s.getBytes());
//        byte[] buff = new byte[1024*10];
//        int len = 0;
//        int index = 1;
//        while((len=is.read(buff))!=-1){
//            System.out.println((index++) + " " + Arrays.toString(buff));
//            out.write(buff, 0, len);
//        }
//        is.close();
//        out.close();

//cache.allocateTiny(heapArena1, buf, 11, 16);
