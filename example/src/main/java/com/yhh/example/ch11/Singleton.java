package com.yhh.example.ch11;

import io.netty.handler.timeout.ReadTimeoutException;

/**
 * @see ReadTimeoutException
 * @see MqttEncoder   找不到这个类呢
 */


public class Singleton {
    private static Singleton singleton;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
