package com.yhh.moni.common;

import com.yhh.moni.EventExecutor;

public interface EventExecutorChooserFactory {

    EventExecutorChooser newChooser(EventExecutor[] executors);

    interface EventExecutorChooser {

        EventExecutor next();
    }
}