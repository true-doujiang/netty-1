package com.yhh.decoder.server;

import com.yhh.decoder.MyProtocolBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyProtocolBean myProtocolBean = (MyProtocolBean)msg;  //直接转化成协议消息实体
        System.out.println("ServerHandler.MyProtocolBean");
        System.out.println(myProtocolBean.getContent());
        System.out.println(myProtocolBean.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}