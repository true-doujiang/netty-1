package com.yhh.example.ch6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author
 */
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 对客户端发送过来的数据msg做处理
     * 若是最后一个handler则msg就不应该往下传了，传到Tail会打印个警告日志并且释放msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(Thread.currentThread().getName() + " AuthHandler: " + msg);

        //msg = null;
        ReferenceCountUtil.release(msg);

        // 所以这里不用往下传了
        //ctx.fireChannelRead(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf password) throws Exception {
        if (paas(password)) {
            ctx.pipeline().remove(this);
        } else {
            ctx.close();
        }
    }

    private boolean paas(ByteBuf password) {
        return false;
    }
}