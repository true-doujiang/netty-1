package com.yhh.moni.channel.socket.nio;

import com.yhh.moni.channel.Channel;
import com.yhh.moni.channel.ChannelPipeline;
import com.yhh.moni.channel.nio.AbstractNioByteChannel;
import io.netty.channel.ChannelException;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioSocketChannel extends AbstractNioByteChannel {



    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSocketChannel.class);

    //
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();



    // 创建JDK nio SocketChannel
    private static SocketChannel newSocket(SelectorProvider provider) {
        try {
            /**
             *  Use the {@link SelectorProvider} to open {@link SocketChannel} and so remove condition in
             *  {@link SelectorProvider#provider()} which is called by each SocketChannel.open() otherwise.
             *
             *  See <a href="https://github.com/netty/netty/issues/2308">#2308</a>.
             */
            return provider.openSocketChannel();
        } catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

//    private final SocketChannelConfig config;

    /**
     * Create a new instance
     */
    public NioSocketChannel() {
        this(DEFAULT_SELECTOR_PROVIDER);
    }

    /**
     * Create a new instance using the given {@link SelectorProvider}.
     */
    public NioSocketChannel(SelectorProvider provider) {
        this(newSocket(provider));
    }

    /**
     * Create a new instance using the given {@link SocketChannel}.
     */
    public NioSocketChannel(SocketChannel socket) {
        this(null, socket);
    }

    /**
     * Create a new instance
     *
     * @param parent    the {@link Channel} which created this instance or {@code null} if it was created by the user
     * @param socket    the {@link SocketChannel} which will be used
     */
    public NioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
        // 绑定配置类
//        config = new NioSocketChannelConfig(this, socket.socket());
    }

//    @Override
//    public ServerSocketChannel parent() {
//        return (ServerSocketChannel) super.parent();
//    }
    @Override
    public Channel parent() {
        return null;
    }

    @Override
    public ChannelPipeline pipeline() {
        return null;
    }
}