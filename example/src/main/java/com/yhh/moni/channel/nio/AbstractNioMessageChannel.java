package com.yhh.moni.channel.nio;

import com.yhh.moni.channel.Channel;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel {




    boolean inputShutdown;

    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }

    /**
     *
     */
    @Override
    protected AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe();
    }




    /**
     * AbstractNioUnsafe 实现类
     *
     * 服务端read() 负责读入新的客户端链接
     */
    private final class NioMessageUnsafe extends AbstractNioUnsafe {

        private final List<Object> readBuf = new ArrayList<Object>();

        @Override
        public void read() {

            assert eventLoop().inEventLoop();
        }

    }
    //----------- NioMessageUnsafe over


}