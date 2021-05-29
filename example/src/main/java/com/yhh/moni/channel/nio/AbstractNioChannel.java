package com.yhh.moni.channel.nio;

import com.yhh.moni.NioEventLoop;
import com.yhh.moni.channel.AbstractChannel;
import com.yhh.moni.channel.Channel;
import io.netty.channel.*;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel extends AbstractChannel {



    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);

    private static final ClosedChannelException DO_CLOSE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(
            new ClosedChannelException(), AbstractNioChannel.class, "doClose()");

    /**
     * JDK ServerSocketChannel 或者 SocketChannel
     */
    private final SelectableChannel ch;
    //将来要关心的事件
    protected final int readInterestOp;


    // 注册时返回的 SelectionKey
    volatile SelectionKey selectionKey;

    boolean readPending;

//    private final Runnable clearReadPendingRunnable = new Runnable() {
//        @Override
//        public void run() {
//            clearReadPending0();
//        }
//    };

    /**
     * The future of the current connection attempt.  If not null, subsequent
     * connection attempts will fail.
     */
//    private ChannelPromise connectPromise;
//    private ScheduledFuture<?> connectTimeoutFuture;
//    private SocketAddress requestedRemoteAddress;

    /**
     * 构造器
     *
     */
    protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {

        super(parent);

        this.ch = ch;
        this.readInterestOp = readInterestOp;
        try {
            ch.configureBlocking(false);
        } catch (IOException e) {
            try {
                ch.close();
            } catch (IOException e2) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to close a partially initialized socket.", e2);
                }
            }

            throw new ChannelException("Failed to enter non-blocking mode.", e);
        }
    }


    @Override
    public NioUnsafe unsafe() {
        return (NioUnsafe) super.unsafe();
    }


    protected SelectableChannel javaChannel() {
        return ch;
    }


    @Override
    public NioEventLoop eventLoop() {
        return (NioEventLoop) super.eventLoop();
    }


    protected SelectionKey selectionKey() {
        assert selectionKey != null;
        return selectionKey;
    }


    public interface NioUnsafe extends Unsafe {
        /**
         * Return underlying {@link SelectableChannel}
         */
        SelectableChannel ch();

        /**
         * Finish connect
         */
        void finishConnect();

        /**
         * 俩个实现类
         * NioByteUnsafe    客户端读取数据
         * NioMessageUnsafe 服务端读取新的连接
         *
         * Read from underlying {@link SelectableChannel}
         */
        void read();

        void forceFlush();
    }


    /**
     * NioUnsafe 内部实现类
     */
    protected abstract class AbstractNioUnsafe extends AbstractChannel.AbstractUnsafe
                                            implements AbstractNioChannel.NioUnsafe {

        protected final void removeReadOp() {
            SelectionKey key = selectionKey();
            // Check first if the key is still valid as it may be canceled as part of the deregistration
            // from the EventLoop
            // See https://github.com/netty/netty/issues/2104
            if (!key.isValid()) {
                return;
            }
            int interestOps = key.interestOps();
            if ((interestOps & readInterestOp) != 0) {
                // only remove readInterestOp if needed
                key.interestOps(interestOps & ~readInterestOp);
            }
        }

        @Override
        public final SelectableChannel ch() {
            return javaChannel();
        }


//        private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
//            if (promise == null) {
//                // Closed via cancellation and the promise has been notified already.
//                return;
//            }
//
//            // Get the state as trySuccess() may trigger an ChannelFutureListener that will close the Channel.
//            // We still need to ensure we call fireChannelActive() in this case.
//            boolean active = isActive();
//
//            // trySuccess() will return false if a user cancelled the connection attempt.
//            boolean promiseSet = promise.trySuccess();
//
//            // Regardless if the connection attempt was cancelled, channelActive() event should be triggered,
//            // because what happened is what happened.
//            if (!wasActive && active) {
//                pipeline().fireChannelActive();
//            }
//
//            // If a user cancelled the connection attempt, close the channel, which is followed by channelInactive().
//            if (!promiseSet) {
//                close(voidPromise());
//            }
//        }

        private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
            if (promise == null) {
                // Closed via cancellation and the promise has been notified already.
                return;
            }

            // Use tryFailure() instead of setFailure() to avoid the race against cancel().
            promise.tryFailure(cause);
//            closeIfClosed();
        }

        @Override
        public final void finishConnect() {
            // Note this method is invoked by the event loop only if the connection attempt was
            // neither cancelled nor timed out.

            assert eventLoop().inEventLoop();
//
//            try {
//                boolean wasActive = isActive();
//                doFinishConnect();
//                fulfillConnectPromise(connectPromise, wasActive);
//            } catch (Throwable t) {
//                fulfillConnectPromise(connectPromise, annotateConnectException(t, requestedRemoteAddress));
//            } finally {
//                // Check for null as the connectTimeoutFuture is only created if a connectTimeoutMillis > 0 is used
//                // See https://github.com/netty/netty/issues/1770
//                if (connectTimeoutFuture != null) {
//                    connectTimeoutFuture.cancel(false);
//                }
//                connectPromise = null;
//            }
        }


        @Override
        public final void forceFlush() {
            // directly call super.flush0() to force a flush now
//            super.flush0();
        }

        private boolean isFlushPending() {
            SelectionKey selectionKey = selectionKey();
            return selectionKey.isValid() && (selectionKey.interestOps() & SelectionKey.OP_WRITE) != 0;
        }

    }
    // -----------------------AbstractNioUnsafe over



}
