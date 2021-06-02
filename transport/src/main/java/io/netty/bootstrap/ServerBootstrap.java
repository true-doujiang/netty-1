/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * {@link Bootstrap} sub-class which allows easy bootstrap of {@link ServerChannel}
 *
 */
public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);

    // 针对接入的客户端channel
    private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<ChannelOption<?>, Object>();
    private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap<AttributeKey<?>, Object>();

    // 客户端相同
    private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);

    // workerGroup 负责IO 读写
    private volatile EventLoopGroup childGroup;
    // 用户代码中 childHandler(xx) 设置的
    private volatile ChannelHandler childHandler;


    /**
     * 构造器
     */
    public ServerBootstrap() { }

    private ServerBootstrap(ServerBootstrap bootstrap) {
        super(bootstrap);
        childGroup = bootstrap.childGroup;
        childHandler = bootstrap.childHandler;
        synchronized (bootstrap.childOptions) {
            childOptions.putAll(bootstrap.childOptions);
        }
        synchronized (bootstrap.childAttrs) {
            childAttrs.putAll(bootstrap.childAttrs);
        }
    }

    /**
     * Specify the {@link EventLoopGroup} which is used for the parent (acceptor) and the child (client).
     */
    @Override
    public ServerBootstrap group(EventLoopGroup group) {
        return group(group, group);
    }

    /**
     * Set the {@link EventLoopGroup} for the parent (acceptor) and the child (client). These
     * {@link EventLoopGroup}'s are used to handle all the events and IO for {@link ServerChannel} and
     * {@link Channel}'s.
     */
    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        if (childGroup == null) {
            throw new NullPointerException("childGroup");
        }
        if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        }
        this.childGroup = childGroup;
        return this;
    }

    /**
     * Allow to specify a {@link ChannelOption} which is used for the {@link Channel} instances once they get created
     * (after the acceptor accepted the {@link Channel}). Use a value of {@code null} to remove a previous set
     * {@link ChannelOption}.
     */
    public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
        if (childOption == null) {
            throw new NullPointerException("childOption");
        }
        if (value == null) {
            synchronized (childOptions) {
                childOptions.remove(childOption);
            }
        } else {
            synchronized (childOptions) {
                childOptions.put(childOption, value);
            }
        }
        return this;
    }

    /**
     * Set the specific {@link AttributeKey} with the given value on every child {@link Channel}. If the value is
     * {@code null} the {@link AttributeKey} is removed
     */
    public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
        if (childKey == null) {
            throw new NullPointerException("childKey");
        }
        if (value == null) {
            childAttrs.remove(childKey);
        } else {
            childAttrs.put(childKey, value);
        }
        return this;
    }

    /**
     * Set the {@link ChannelHandler} which is used to serve the request for the {@link Channel}'s.
     */
    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        if (childHandler == null) {
            throw new NullPointerException("childHandler");
        }
        this.childHandler = childHandler;
        return this;
    }

    /**
     * 初始化服务端 NioServerSocketChannel
     */
    @Override
    void init(Channel channel) throws Exception {

        // 服务端 配置 属性
        final Map<ChannelOption<?>, Object> options = options0();
        synchronized (options) {
            setChannelOptions(channel, options, logger);
        }

        final Map<AttributeKey<?>, Object> attrs = attrs0();
        synchronized (attrs) {
            for (Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
                @SuppressWarnings("unchecked")
                AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
                channel.attr(key).set(e.getValue());
            }
        }

        ChannelPipeline p = channel.pipeline();

        // worker group
        final EventLoopGroup currentChildGroup = childGroup;
        final ChannelHandler currentChildHandler = childHandler;

        final Entry<ChannelOption<?>, Object>[] currentChildOptions;
        final Entry<AttributeKey<?>, Object>[] currentChildAttrs;

        // 客户端 配置 属性
        synchronized (childOptions) {
            currentChildOptions = childOptions.entrySet().toArray(newOptionArray(0));
        }
        synchronized (childAttrs) {
            currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(0));
        }

        /**
         * 匿名内部类 会被添加到pipeline的 pendingHandlerCallbackHead; 属性中
         */
        // 这是 pipeline 创建完成后第一个添加的 handler 所以会被放到 pipeline的pendingHandlerCallbackHead属性中
        ChannelInitializer initializerHandler = new ChannelInitializer<Channel>() {

            public String myName = "ServerBootstrap的ChannelInitializer匿名内部类";

            // ChannelInitializer 父类中有 handlerAdded(ctx) 它调用了initChannel(ch)
            @Override
            public void initChannel(final Channel ch) throws Exception {

                System.out.println(Thread.currentThread().getName() + " ServerBootstrap initializerHandler initChannel() ch  = " + ch);

                final ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = config.handler();

                if (handler != null) {
                    //pipeline.addLast(handler);  // 如果给服务端配置了Handler 则这个时候添加到服务端ch的pipeline
                    pipeline.addLast(null, "serverHandler", handler);
                }

                // ch 是服务端 配置ServerBootstrapAcceptor 用于给新接入的客户端channel分配线程
                Runnable r = new Thread("ServerBootstrapAcceptor task 一个特殊的Handler") {
                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread().getName() + " ServerBootstrapAcceptor task 一个特殊的Handler 被执行了");
                        // ServerBootstrapAcceptor 一个特殊的Handler
                        ServerBootstrapAcceptor acceptorHandler = new ServerBootstrapAcceptor(
                                ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs);
                        //pipeline.addLast(acceptorHandler);
                        pipeline.addLast(null, "acceptorHandler", acceptorHandler);
                    }
                };

                System.out.println(Thread.currentThread().getName() + " 一个特殊的Handler 添加到任务队列,等待执行 task = " + r);

                // r 会被添加到taskQueue中
                ch.eventLoop().execute(r);
            }
        };

        System.out.println(Thread.currentThread().getName() + " ServerBootstrap initializerHandler = " + initializerHandler);
        // 添加一个特殊的 ChannelHandler 这个hander会被添加到 DefaultChannelPipeline 的 pendingHandlerCallbackHead
        //p.addLast(initializerHandler);
        // 这应该是第一个 往pipeline中添加的 handler 所以会被放到 pipeline的pendingHandlerCallbackHead属性中
        p.addLast(null, "initializerHandler", initializerHandler);
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        if (childHandler == null) {
            throw new IllegalStateException("childHandler not set");
        }
        if (childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            childGroup = config.group();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private static Entry<AttributeKey<?>, Object>[] newAttrArray(int size) {
        return new Entry[size];
    }

    @SuppressWarnings("unchecked")
    private static Map.Entry<ChannelOption<?>, Object>[] newOptionArray(int size) {
        return new Map.Entry[size];
    }

    /**
     * inBound
     * 这个ChannelHandler放在服务端channel的Pipeline中 专门对新接入客户单的channel处理
     */
    private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {
        // workerGroup
        private final EventLoopGroup childGroup;
        // childHandler 用户代码设置到外部类，外部类传进来
        private final ChannelHandler childHandler;

        private final Entry<ChannelOption<?>, Object>[] childOptions;
        private final Entry<AttributeKey<?>, Object>[] childAttrs;

        private final Runnable enableAutoReadTask;

        /**
         * 构造器
         */
        ServerBootstrapAcceptor(
                final Channel channel, EventLoopGroup childGroup, ChannelHandler childHandler,
                Entry<ChannelOption<?>, Object>[] childOptions, Entry<AttributeKey<?>, Object>[] childAttrs) {
            this.childGroup = childGroup;
            this.childHandler = childHandler;
            this.childOptions = childOptions;
            this.childAttrs = childAttrs;

            // Task which is scheduled to re-enable auto-read.
            // It's important to create this Runnable before we try to submit it as otherwise the URLClassLoader may
            // not be able to load the class because of the file limit it already reached.
            //
            // See https://github.com/netty/netty/issues/1328
            enableAutoReadTask = new Runnable() {
                @Override
                public void run() {
                    channel.config().setAutoRead(true);
                }
            };
        }

        // 我自己加的
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println(Thread.currentThread().getName() + " ServerBootstrapAcceptor.handlerAdded(ctx)");
        }

        // 我自己加的
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println(Thread.currentThread().getName() + " ServerBootstrapAcceptor.channelActive(ctx)");
            ctx.fireChannelActive();
        }



        /**
         * channelRead() 就没有调用父类方法 pipeline到此结束   TailContext节点就没有执行，我说最后一个警告怎么没有打印呢
         */
        @Override
        @SuppressWarnings("unchecked")
        public void channelRead(ChannelHandlerContext ctx, Object msg) {

            System.out.println(Thread.currentThread().getName() + " ServerBootstrapAcceptor = " + this + " channelRead(ctx, msg) 执行");
            // 我自己加点
            ctx.fireChannelRead(msg);

            final Channel childChannel = (Channel) msg;

            //childChannel.pipeline().addLast(childHandler);
            childChannel.pipeline().addLast(null, "childHandler", childHandler);

            System.out.println(Thread.currentThread().getName() + " 给客户端channel= " + childChannel + " add 用户代码中的 childHandler= " + childHandler);

            setChannelOptions(childChannel, childOptions, logger);

            for (Entry<AttributeKey<?>, Object> e: childAttrs) {
                childChannel.attr((AttributeKey<Object>) e.getKey()).set(e.getValue());
            }

            try {
                // 对客户单channel 做类似服务端channel同样操作流程
                ChannelFuture register = childGroup.register(childChannel);

                ChannelFutureListener listener = new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            forceClose(childChannel, future.cause());
                        }
                    }
                };

                register.addListener(listener);
            } catch (Throwable t) {
                forceClose(childChannel, t);
            }

            // 源码中没有  说执行到这个hander就不会执行后面hander的channelRead()
            //ctx.fireChannelRead(msg);
        }

        private static void forceClose(Channel child, Throwable t) {
            child.unsafe().closeForcibly();
            logger.warn("Failed to register an accepted channel: {}", child, t);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final ChannelConfig config = ctx.channel().config();
            if (config.isAutoRead()) {
                // stop accept new connections for 1 second to allow the channel to recover
                // See https://github.com/netty/netty/issues/1328
                config.setAutoRead(false);
                ctx.channel().eventLoop().schedule(enableAutoReadTask, 1, TimeUnit.SECONDS);
            }
            // still let the exceptionCaught event flow through the pipeline to give the user
            // a chance to do something with it
            ctx.fireExceptionCaught(cause);
        }
    }
    //--------------------ServerBootstrapAcceptor

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public ServerBootstrap clone() {
        return new ServerBootstrap(this);
    }

    /**
     * Return the configured {@link EventLoopGroup} which will be used for the child channels or {@code null}
     * if non is configured yet.
     *
     * @deprecated Use {@link #config()} instead.
     */
    @Deprecated
    public EventLoopGroup childGroup() {
        return childGroup;
    }

    final ChannelHandler childHandler() {
        return childHandler;
    }

    final Map<ChannelOption<?>, Object> childOptions() {
        return copiedMap(childOptions);
    }

    final Map<AttributeKey<?>, Object> childAttrs() {
        return copiedMap(childAttrs);
    }

    @Override
    public final ServerBootstrapConfig config() {
        return config;
    }
}
