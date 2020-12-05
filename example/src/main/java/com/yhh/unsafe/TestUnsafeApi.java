package com.yhh.unsafe;

import sun.misc.Unsafe;

import java.util.Arrays;

public class TestUnsafeApi {

    public static  Unsafe UNSAFE = UnSafeUtil.getUnsafe();

    public static void main(String[] args) throws Exception {
        UnsafeTestKlass klass = new UnsafeTestKlass();

        long staticFieldOffset = UNSAFE.staticFieldOffset(UnsafeTestKlass.class.getDeclaredField("number"));
        long unstaticFieldOffset = UNSAFE.objectFieldOffset(klass.getClass().getDeclaredField("name"));
        long helloFieldOffset = UNSAFE.objectFieldOffset(klass.getClass().getDeclaredField("hello"));

        System.out.println("静态变量相对于类内存地址的偏移量 = " + staticFieldOffset);
        System.out.println("非静态变量相对于实例化对象的偏移量 = " + unstaticFieldOffset);

        UNSAFE.putInt(UnsafeTestKlass.class, staticFieldOffset, 10);
        //2.2 添加非基本数据类型的值，使用putObject(值类型的类类型 ， 内存地址 , 值对象);
        UNSAFE.putObject(klass, unstaticFieldOffset, "哈哈");
        UNSAFE.putObject(klass, helloFieldOffset, new Hello());

        UnsafeTestKlass klassOther = new UnsafeTestKlass();
        UNSAFE.putObject(klassOther, unstaticFieldOffset, "哈哈");

        System.out.println("静态变量被修改后的值 = " + UnsafeTestKlass.number);
        System.out.println("非静态变量被修改后的值 = " + klass.name);

        int anInt = UNSAFE.getInt(UnsafeTestKlass.class, staticFieldOffset);
        //2.3 获取object类型值
        Object o = UNSAFE.getObject(klass, unstaticFieldOffset);
        Hello h = (Hello) UNSAFE.getObject(klass, helloFieldOffset);
        System.out.println(h);
        h.name = "胖小子";
        long hnameFieldOffset = UNSAFE.objectFieldOffset(h.getClass().getDeclaredField("name"));
        UNSAFE.putObject(h, hnameFieldOffset, "乖乖滴");


        Object[] array = new Object[] {klass};
        System.out.println(Arrays.toString(array));
        // 为毛每次都是16
        long baseOffset = UNSAFE.arrayBaseOffset(Object[].class);
        UNSAFE.putObject(array, baseOffset, "第一个元素");
        System.out.println(Arrays.toString(array));
        Object[] array2 = new Object[10];
        UNSAFE.putObject(array2, baseOffset, "第一个元素");
        int scale = UNSAFE.arrayIndexScale(Object[].class);
        UNSAFE.putObject(array2, baseOffset + scale, "第一个元素2");
        // 数组操作
        byte[] data = new byte[10];
        long byteArrayBaseOffset = UNSAFE.arrayBaseOffset(byte[].class);
        UNSAFE.putByte(data, byteArrayBaseOffset, (byte) 1);
        UNSAFE.putByte(data, byteArrayBaseOffset + 5, (byte) 5);





        //说明：该内存的使用将直接脱离jvm，gc将无法管理以下方式申请的内存，以用于一定要手动释放内存，避免内存溢出；
        //2.1 向本地系统申请一块内存地址； 使用方法allocateMemory(long capacity) ,该方法将返回内存地址的起始地址
        long address = UNSAFE.allocateMemory(8);
        System.out.println("allocate memory address = " + address);

        //2.2  向内存地址中设置值；
        //2.2 说明： 基本数据类型的值的添加，使用对应put数据类型方法，如：添加byte类型的值，使用：putByte(内存地址 , 值);
        UNSAFE.putByte(address, (byte)100);

        //2.3 从给定的内存地址中取出值, 同存入方法基本类似，基本数据类型使用getXX(地址) ，object类型使用getObject(类类型，地址);
        byte b = UNSAFE.getByte(address);
        System.out.println(b);


        //2.4 重新分配内存 reallocateMemory(内存地址 ，大小) , 该方法说明 ：该方法将释放掉给定内存地址所使用的内存，并重新申请给定大小的内存；
        // 注意： 会释放掉原有内存地址 ，但已经获取并保存的值任然可使用，原因：个人理解：使用unsafe.getXXX方法获取的是该内存地址的值，
        //并把值赋值给左边对象，这个过程相当于是一个copy过程--- 将系统内存的值 copy 到jvm 管理的内存中；
//        long newAddress = UNSAFE.reallocateMemory(address, 32);
//        System.out.println("new address = "+ newAddress);
        //再次调用，内存地址的值已丢失； 被保持与jvm中的对象值不被丢失；
//        System.out.println("local memory value =" + UNSAFE.getByte(address) + " jvm memory value = "+ b);

        //2.5 使用申请过的内存；
        //说明： 该方法同reallocateMemory 释放内存的原理一般；
       // UNSAFE.freeMemory(newAddress);

        //2.5 put 方法额外说明
        //putXXX() 方法中存在于这样的重载： putXXX(XXX ,long , XXX) ,如：putInt(Integer ,long , Integer) 或者 putObject(Object ,long ,Object)
        //个人理解 : 第一个参数相当于作用域，即：第三个参数所代表的值，将被存储在该域下的给定内存地址中；(此处疑惑：
        //如果unsafe是从操作系统中直接获取的内存地址，那么该地址应该唯一，重复在该地址存储数据，后者应该覆盖前者，但是并没有；应该是jvm有特殊处理，暂未研究深入，所以暂时理解为域；)
        //以下示例可以说明，使用allocateMemory申请的同一地址，并插入不同对象所表示的值，后面插入的值并没有覆盖前面插入的值；
        //
//        long taddress = unsafe.allocateMemory(1);
//        Hello l = new Hello("l");
//        Hello l1 = new Hello("l1");
//        unsafe.putObject(l, taddress, l);
//        System.out.println(unsafe.getObject(l, taddress));
//        unsafe.putObject(l1, taddress, l1);
//        System.out.println(unsafe.getObject(l1, taddress));
//        System.out.println(unsafe.getObject(l, taddress));
//        unsafe.putObject(Hello.class, taddress, new Hello("33"));
//        System.out.println(unsafe.getObject(Hello.class, taddress));

//        unsafe.freeMemory(taddress);
    }
}




class UnsafeTestKlass {

    public int value;
    static public int number = 5;
    public String name;
    public Hello hello;

    public UnsafeTestKlass() {
        this(99);
    }

    public UnsafeTestKlass(int value) {
        this.value = value;
    }


    public int value() {
        return value;
    }
}


class Hello {

    public int value;
    static public int number = 5;
    public String name;

    public Hello() {
        System.out.println(" 默认构造器" );
    }

    public Hello(String name) {
        System.out.println("name = " + name );
        this.name = name;
    }

}