package com.yhh.moni.channel.nio;

import com.yhh.moni.channel.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel extends AbstractNioChannel {


    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);

    private static final String EXPECTED_TYPES =
            " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " +
                    StringUtil.simpleClassName(FileRegion.class) + ')';

//    private final Runnable flushTask = new Runnable() {
//        @Override
//        public void run() {
//            // Calling flush0 directly to ensure we not try to flush messages that were added via write(...) in the
//            // meantime.
//            ((AbstractNioChannel.AbstractNioUnsafe) unsafe()).flush0();
//        }
//    };

    private boolean inputClosedSeenErrorOnRead;

    /**
     * 构造器
     * 客户端channel父类， 关心OP_READ事件
     */
    protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super(parent, ch, SelectionKey.OP_READ);
    }

    @Override
    protected AbstractNioUnsafe newUnsafe() {
        return new NioByteUnsafe();
    }

    /**
     * AbstractNioUnsafe 实现类
     */
    protected class NioByteUnsafe extends AbstractNioChannel.AbstractNioUnsafe {

//        private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close,
//                                         RecvByteBufAllocator.Handle allocHandle) {
//            if (byteBuf != null) {
//                if (byteBuf.isReadable()) {
//                    readPending = false;
//                    pipeline.fireChannelRead(byteBuf);
//                } else {
//                    byteBuf.release();
//                }
//            }
//            allocHandle.readComplete();
//            pipeline.fireChannelReadComplete();
//            pipeline.fireExceptionCaught(cause);
//            if (close || cause instanceof IOException) {
//                closeOnRead(pipeline);
//            }
//        }

        /**
         * NioUnsafe 接口中定义
         * 客户端读取数据
         */
        @Override
        public final void read() {

        }

    }


}