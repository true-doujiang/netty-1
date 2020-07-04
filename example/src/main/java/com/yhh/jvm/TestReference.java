package com.yhh.jvm;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 类Test的实现描述：TODO 类实现描述
 *
 * @author youhuanhuan 2020-07-01 20:51
 */
public class TestReference {

    private static List<Object> list = new ArrayList();


    public static void main(String[] args) {
        //testStrongReference();
        //testSoftReference();
        testWeakReference();
    }

    private static void testStrongReference() {
        // 当 new byte为 1M 时，程序运行正常
        byte[] buff = new byte[1024 * 1024 * 3];
        System.out.println(buff);
    }

    private static void testSoftReference() {
        byte[] buff = null;  // 定义在这里就会OutOfMemoryError
        for (int i = 0; i < 10; i++) {
            buff = new byte[1024 * 1024 * 1];
            SoftReference<byte[]> sr = new SoftReference<>(buff);
            System.out.println("i = " + i);
            list.add(sr);
        }

        System.gc(); //主动通知垃圾回收

        for(int i=0; i < list.size(); i++){
            Object obj = ((SoftReference) list.get(i)).get();
            System.out.println(obj);
        }
    }


    private static void testWeakReference() {
        byte[] buff = null;  // 定义在这里就会OutOfMemoryError
        for (int i = 0; i < 10; i++) {
            buff = new byte[1024 * 1024];
            WeakReference<byte[]> sr = new WeakReference<>(buff);
            list.add(sr);
        }

        System.gc(); //主动通知垃圾回收

        for(int i=0; i < list.size(); i++){
            Object obj = ((WeakReference) list.get(i)).get();
            System.out.println(obj);
        }
    }


}
