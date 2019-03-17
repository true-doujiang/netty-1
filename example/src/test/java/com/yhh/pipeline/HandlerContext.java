package com.yhh.pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class HandlerContext {

    private ExecutorService executor = Executors.newCachedThreadPool();

    private Handler handler;
    //下一个context的引用
    private HandlerContext next;

    public HandlerContext(Handler handler) {
        this.handler = handler;
    }

    public void setNext(HandlerContext ctx) {
        this.next = ctx;
    }

    /**
     * 执行任务的时候向线程池提交一个runnable的任务，任务中调用handler
     */
    public void doWork(final Object msg) {
        // next==null说明是最后一个默认的Handler
        if (next == null) {
            return;
        } else {
//            executor.submit(new Runnable() {
//                @Override
//                public void run() {
//                    ////把下一个handler的context穿个handler来实现回调
//                    handler.channelRead(next, msg);
//                }
//            });
        }
        handler.channelRead(next,msg);
    }

    /**
     * 这里的write操作是给handler调用的，实际上是一个回调方法，当handler处理完数据之后，
     * 调用一下nextcontext.write，此时就把任务传递给下一个handler了。
     */
    public void write(Object msg) {
        doWork(msg);
    }
}
