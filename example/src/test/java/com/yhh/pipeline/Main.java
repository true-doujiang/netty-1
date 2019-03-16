package com.yhh.pipeline;

public class Main {


    public static void main(String[] args) {
        MyPipeline pipeline = new MyPipeline();
        pipeline.addFirst(new TestHandler3());
        pipeline.addFirst(new TestHandler2());
        pipeline.addFirst(new TestHandler1());

        pipeline.Request("hello world " );
        //提交多个任务
//        for (int i = 0; i < 10; i++) {
//            pipeline.Request("hello" + i);
//        }
    }

}
