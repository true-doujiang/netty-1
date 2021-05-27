package com.yhh.moni;



import com.yhh.moni.common.DefaultEventExecutorChooserFactory;
import com.yhh.moni.common.DefaultThreadFactory;
import com.yhh.moni.common.EventExecutorChooserFactory;
import com.yhh.moni.common.ThreadPerTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class MultithreadEventExecutorGroup  extends AbstractEventExecutorGroup {


    private final EventExecutor[] children;
    private final EventExecutorChooserFactory.EventExecutorChooser chooser;




    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, Object... args) {
        this(nThreads, executor, DefaultEventExecutorChooserFactory.INSTANCE, args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor,
                                            EventExecutorChooserFactory chooserFactory, Object... args) {

        // 默认使用线程工厂是 DefaultThreadFactory
        if (executor == null) {
            ThreadFactory threadFactory = newDefaultThreadFactory();
            executor = new ThreadPerTaskExecutor(threadFactory);
        }

        children = new EventExecutor[nThreads];

        for (int i = 0; i < nThreads; i ++) {
            boolean success = false;
            try {
                children[i] = newChild(executor, args);
                success = true;
            } catch (Exception e) {
                throw new IllegalStateException("failed to create a child event loop", e);
            }
        }

        chooser = chooserFactory.newChooser(children);
    }


    protected ThreadFactory newDefaultThreadFactory() {
        Class<? extends MultithreadEventExecutorGroup> clazz = getClass();
        DefaultThreadFactory defaultThreadFactory = new DefaultThreadFactory(clazz, Thread.MAX_PRIORITY);
        return defaultThreadFactory;
    }


    /**
     * 选择一个 NioEventLoop
     */
    @Override
    public EventExecutor next() {
        return chooser.next();
    }


    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;

}
