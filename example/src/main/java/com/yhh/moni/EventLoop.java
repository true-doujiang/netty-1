package com.yhh.moni;


/**
 *
 */
public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {

    // 之所以报错是因为没有  OrderedEventExecutor extends EventExecutor  这层关系
    // EventExecutor中定义（OrderedEventExecutor的父接口）
    @Override
    EventLoopGroup parent();

}
