package com.yhh.moni2;

import io.netty.buffer.ByteBuf;

/**
 * https://blog.csdn.net/crwen_/article/details/102944308
 * 在初始化一个类时，并不会初始化它所实现的接口
 * 在初始化一个接口时，并不会先初始化它的父接口
 */
public class ZMyTest {

    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);

    public static void main(String[] args) {
        System.out.println("BYTE_BUF_FROM_SOMEWHERE = " + BYTE_BUF_FROM_SOMEWHERE);
    }

}
