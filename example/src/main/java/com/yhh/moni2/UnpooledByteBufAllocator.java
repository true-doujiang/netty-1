package com.yhh.moni2;


import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;

public class UnpooledByteBufAllocator extends AbstractByteBufAllocator {

    private final boolean disableLeakDetector;
    private final boolean noCleaner;
    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(true);



    public UnpooledByteBufAllocator(boolean preferDirect) {
        this(preferDirect, false);
    }

    public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector) {
        this(preferDirect, disableLeakDetector, true);
    }

    public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector, boolean tryNoCleaner) {
        super(preferDirect);
        this.disableLeakDetector = disableLeakDetector;
        noCleaner = tryNoCleaner && PlatformDependent.hasUnsafe() && PlatformDependent.hasDirectBufferNoCleanerConstructor();
    }


    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        boolean b = PlatformDependent.hasUnsafe();
//        if (b) {
//            return new InstrumentedUnpooledUnsafeHeapByteBuf(this, initialCapacity, maxCapacity);
//        } else {
//            return new InstrumentedUnpooledHeapByteBuf(this, initialCapacity, maxCapacity);
//        }
        return null;
    }


}
