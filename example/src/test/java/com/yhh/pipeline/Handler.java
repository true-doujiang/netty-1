package com.yhh.pipeline;

public interface Handler {
    void channelRead(HandlerContext ctx, Object msg);
}
