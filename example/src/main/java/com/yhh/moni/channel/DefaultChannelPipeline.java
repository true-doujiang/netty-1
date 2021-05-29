package com.yhh.moni.channel;


import com.yhh.moni.EventExecutor;
import com.yhh.moni.EventExecutorGroup;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;




public class DefaultChannelPipeline implements ChannelPipeline {


    static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);


    private static final String HEAD_NAME = generateName0(HeadContext.class);
    private static final String TAIL_NAME = generateName0(TailContext.class);


    // 每个pipeline实例都要有头尾节点  构造器中初始化
    final AbstractChannelHandlerContext head;
    final AbstractChannelHandlerContext tail;

    // channel 的整个生命周期内会绑定一个 ChannelPipeline
    private final Channel channel;


    /**
     * Set to {@code true} once the {@link AbstractChannel} is registered.Once set to {@code true} the value will never
     * change.
     */
    private boolean registered;



    /**
     * constructor
     *
     * Pipeline 链表数据结构，节点元素 xxx..HandlerContext
     */
    protected DefaultChannelPipeline(Channel channel) {
        // 不可以为null
        this.channel = ObjectUtil.checkNotNull(channel, "channel");


        // 内部类 inbound
        tail = new TailContext(this);
        // 内部类 inbound outbound
        head = new HeadContext(this);

        head.next = tail;
        tail.prev = head;
    }


    /**
     * 把handler 封装成 handlerContext
     */
    private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
        EventExecutor eventExecutor = childExecutor(group);
        return new DefaultChannelHandlerContext(this, eventExecutor, name, handler);
    }

    /**
     *
     */
    private EventExecutor childExecutor(EventExecutorGroup group) {

        if (group == null) {
            return null;
        }

//        Boolean pinEventExecutor = channel.config().getOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
//        if (pinEventExecutor != null && !pinEventExecutor) {
//            return group.next();
//        }

//        Map<EventExecutorGroup, EventExecutor> childExecutors = this.childExecutors;
//        if (childExecutors == null) {
//            // Use size of 4 as most people only use one extra EventExecutor.
//            childExecutors = this.childExecutors = new IdentityHashMap<EventExecutorGroup, EventExecutor>(4);
//        }
//        // Pin one of the child executors once and remember it so that the same child executor
//        // is used to fire events for the same channel.
//        EventExecutor childExecutor = childExecutors.get(group);
//        if (childExecutor == null) {
//            childExecutor = group.next();
//            childExecutors.put(group, childExecutor);
//        }
//        return childExecutor;
        return null;
    }

    @Override
    public final Channel channel() {
        return channel;
    }

    private static String generateName0(Class<?> handlerType) {
        return StringUtil.simpleClassName(handlerType) + "#0";
    }


    /**
     * Inbound
     *
     * A special catch-all handler that handles both bytes and messages.
     */
    final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {

        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, TAIL_NAME, true, false);
            setAddComplete();
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            System.out.println(Thread.currentThread().getName() + " TailContext = " + this + " handlerAdded(ctx) 执行");
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println(Thread.currentThread().getName() + " TailContext = " + this + " handlerRemoved(ctx) 执行");
        }





        // 个方法都留空
//        @Override
//        public void channelRegistered(ChannelHandlerContext ctx) {
//            System.out.println(Thread.currentThread().getName() + " TailContext = " + this + " channelRegistered(ctx) 执行");
//        }
//
//        @Override
//        public void channelUnregistered(ChannelHandlerContext ctx) {
//            System.out.println(Thread.currentThread().getName() + " TailContext = " + this + " channelUnregistered(ctx) 执行");
//        }
//

//
//
//
//        @Override
//        public void channelActive(ChannelHandlerContext ctx) {
//            System.out.println(Thread.currentThread().getName() + " TailContext = " + this + " channelActive(ctx) 执行");
//            onUnhandledInboundChannelActive();
//        }
//
//        @Override
//        public void channelInactive(ChannelHandlerContext ctx) {
//            onUnhandledInboundChannelInactive();
//        }
//
//        @Override
//        public void channelWritabilityChanged(ChannelHandlerContext ctx) {
//            onUnhandledChannelWritabilityChanged();
//        }
//
//
//
//        @Override
//        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
//            onUnhandledInboundUserEventTriggered(evt);
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//            onUnhandledInboundException(cause);
//        }
//
//        /**
//         *  作为 InBound的最后一个 打个警告日志
//         */
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            System.out.println(Thread.currentThread().getName() + " 作为 InBound的最后一个 打个警告日志 TailContext = " + this + "  channelRead(ctx, msg) 执行");
//            // 都到最后一个read()还没处理 所以打印一个警告日志，或者释放内存
//            onUnhandledInboundMessage(msg);
//        }
//
//        @Override
//        public void channelReadComplete(ChannelHandlerContext ctx) {
//            onUnhandledInboundChannelReadComplete();
//        }

    }

    /**
     * Outbound Inbound
     *
     */
    final class HeadContext extends AbstractChannelHandlerContext implements ChannelOutboundHandler, ChannelInboundHandler {

        private final Channel.Unsafe unsafe;

        HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, HEAD_NAME, true, true);
            unsafe = pipeline.channel().unsafe();
            setAddComplete();
        }


        /******************************
         * ChannelHandler 定义
         * ****************************/
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            // NOOP
            System.out.println(Thread.currentThread().getName() + " HeadContext = " + this + " handlerAdded(ctx) 执行");
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            // NOOP
            System.out.println(Thread.currentThread().getName() + " HeadContext = " + this + " handlerRemoved(ctx) 执行");
        }

        /******************************
         * ChannelHandlerContext 定义
         * ****************************/
        @Override
        public ChannelHandler handler() {
            return this;
        }



    }
    // --------------HeadContext over


}