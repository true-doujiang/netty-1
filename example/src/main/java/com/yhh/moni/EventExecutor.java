package com.yhh.moni;

public interface EventExecutor extends EventExecutorGroup {


    /**
     * Returns a reference to itself.
     */
    @Override
    EventExecutor next();

    /**
     *
     */
    EventExecutorGroup parent();



    boolean inEventLoop();
    boolean inEventLoop(Thread thread);

}
