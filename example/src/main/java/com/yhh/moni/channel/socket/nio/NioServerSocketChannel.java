package com.yhh.moni.channel.socket.nio;

import com.yhh.moni.channel.Channel;
import com.yhh.moni.channel.ChannelPipeline;
import com.yhh.moni.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioServerSocketChannel extends AbstractNioMessageChannel {


    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    //
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);

    // 创建JDK nio ServerSocketChannel
    private static ServerSocketChannel newSocket(SelectorProvider provider) {
        try {
            /**
             *  Use the {@link SelectorProvider} to open {@link SocketChannel} and so remove condition in
             *  {@link SelectorProvider#provider()} which is called by each ServerSocketChannel.open() otherwise.
             *
             *  See <a href="https://github.com/netty/netty/issues/2308">#2308</a>.
             */
            return provider.openServerSocketChannel();
        } catch (IOException e) {
            throw new ChannelException("Failed to open a server socket.", e);
        }
    }

//    private final ServerSocketChannelConfig config;

    /**
     * Create a new instance
     */
    public NioServerSocketChannel() {
        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioServerSocketChannel(SelectorProvider provider) {
        this(newSocket(provider));
    }

    /**
     * Create a new instance using the given {@link ServerSocketChannel}.
     */
    public NioServerSocketChannel(ServerSocketChannel channel) {
        // 服务端channel的parent为null   直接将OP_ACCEPT事件传到父类AbstractNioMessageChannel
        super(null, channel, SelectionKey.OP_ACCEPT);
        // 绑定配置类
//        config = new NioServerSocketChannelConfig(this, javaChannel().socket());
    }

    @Override
    public Channel parent() {
        return null;
    }

    @Override
    public ChannelPipeline pipeline() {
        return null;
    }
}
