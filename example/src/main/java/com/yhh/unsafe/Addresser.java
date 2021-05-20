package com.yhh.unsafe;

import sun.misc.Unsafe;
 
public class Addresser {
    private static Unsafe unsafe = UnSafeUtil.getUnsafe();


    // todo 不明白这个方法
    public static long addressOf(Object o) throws Exception {
 
        Object[] array = new Object[] { o };
 
        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        //arrayBaseOffset方法是一个本地方法，可以获取数组第一个元素的偏移地址
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                //getInt方法获取对象中offset偏移地址对应的int型field的值
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                //getLong方法获取对象中offset偏移地址对应的long型field的值
                Object object = unsafe.getObject(array, baseOffset);
                System.out.println("object = " + object);
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }
        return (objectAddress);
    }
 
    public static void main(String... args) throws Exception {
        //Object mine = "Hello world".toCharArray(); //先把字符串转化为数组对象

        byte[] data = new byte[] {11, 2, 3, 4, 5, 6, 99};
        long address = addressOf(data);
        System.out.println("Addess: " + address);

//        for (long i = 0; i < 10; i++) {
//            int cur = unsafe.getByte(address + i);
//            System.out.println( cur);
//        }

        System.out.println("address ============ ");

        // 这个才能正确获取到数组中的值
        int baseOffset = unsafe.arrayBaseOffset(byte[].class);
        for (int i = 0; i < data.length; i++) {
            byte aByte = unsafe.getByte(data, (long) baseOffset + i);
            // getInt getObject都导致虚拟机报错
            //Object object = unsafe.getInt(data, (long) i + baseOffset);
            System.out.println("aByte = " + aByte);
        }

    }

}