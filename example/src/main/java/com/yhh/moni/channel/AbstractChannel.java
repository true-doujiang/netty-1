package com.yhh.moni.channel;

import com.yhh.moni.EventLoop;

public abstract class AbstractChannel  implements Channel {



    private final Channel parent;

    /**
     * 三大件 每个channel都会有三大件   还有一个NioEventLoop
     */
    private final Channel.Unsafe unsafe;
    private final DefaultChannelPipeline pipeline;



    /**
     * 注册 selector 的时候 初始化了
     * 每个channel上都会有个NioEventLoop 只有这样 channel才能有生命力啊，引擎
     * 要把观念转过来 不是 channel 依赖于NioEventLoop 而是 NioEventLoop依赖于channel
     *
     * 可以通过 eventLoop 的 selector属性找到 他注册的channel
     */
    private volatile EventLoop eventLoop;

    // 当前的channel是否注册到selector 上了
    private volatile boolean registered;

    /**
     * 构造器
     */
    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        // 这个方法定义在本类中 公用
//        id = newId();
        // 抽象方法由具体子类实现， unsafe也是有具体子类实现的
        unsafe = newUnsafe();  //抽象方法
        // protected 实现留给子类实现的余地， 但基本就用本类的实现了
        pipeline = newChannelPipeline();
    }



    protected abstract AbstractUnsafe newUnsafe();

    /**
     * 创建 Pipeline
     */
    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline(this);
    }

    @Override
    public Unsafe unsafe() {
        return unsafe;
    }


    /**
     * 获取当前channel 绑定的 NioEventLoop(线程)
     */
    @Override
    public EventLoop eventLoop() {
        EventLoop eventLoop = this.eventLoop;
        if (eventLoop == null) {
            throw new IllegalStateException("channel not registered to an event loop");
        }
        return eventLoop;
    }

    /**
     *  Unsafe 实现类 功能:
     *  1. channel 注册到 selector 上
     *  2. channel 绑定端口
     */
    protected abstract class AbstractUnsafe implements Unsafe {

    }

}
