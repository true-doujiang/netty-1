package com.yhh.moni;


import java.util.Queue;
import java.util.concurrent.Executor;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {


    private final Queue<Runnable> tailTasks;


    protected SingleThreadEventLoop(EventLoopGroup parent,
                                    Executor executor,
                                    boolean addTaskWakesUp,
                                    int maxPendingTasks,
                                    RejectedExecutionHandler rejectedExecutionHandler) {

        super(parent, executor, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);

        // 干什么用的 tailTasks: MpscUnboundedArrayQueue
        tailTasks = newTaskQueue(maxPendingTasks);
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup) super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }


}
