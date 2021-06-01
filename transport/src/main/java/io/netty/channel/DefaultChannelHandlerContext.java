/*
* Copyright 2014 The Netty Project
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
package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

/**
 * HandlerContext 唯一实现类
 */
final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {


    private final ChannelHandler handler;

    /**
     * 构造器
     */
    DefaultChannelHandlerContext(DefaultChannelPipeline pipeline,
                                 EventExecutor executor,
                                 String name,
                                 ChannelHandler handler) {

        super(pipeline, executor, name, isInbound(handler), isOutbound(handler));

        if (handler == null) {
            throw new NullPointerException("handler");
        }
        this.handler = handler;
    }

    // ChannelHandlerContext 定义，其它方法都是在 AbstractChannelHandlerContext中实现了
    @Override
    public ChannelHandler handler() {
        return handler;
    }

    // 判断是 in 还是 out
    private static boolean isInbound(ChannelHandler handler) {
        return handler instanceof ChannelInboundHandler;
    }

    private static boolean isOutbound(ChannelHandler handler) {
        return handler instanceof ChannelOutboundHandler;
    }
}
