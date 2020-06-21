package com.yhh;

public class TestBinary {

    public static void main(String[] args) {
       f2();
    }

    public static void f1() {
        int x = 0xFFFFFE00;
        //-512
        System.out.println(x);
        //11111111111111111111111000000000
        System.out.println(Integer.toBinaryString(x));

        int y = 512;
        //1000000000
        System.out.println(Integer.toBinaryString(y));

        boolean isLess512 = (512 & 0xFFFFFE00) == 0;
        System.out.println("isLess512: " + isLess512);

        System.out.println("---------------");

        int z = 1;
        z = z<<9;
        System.out.println(z);
        System.out.println(Integer.toBinaryString(z));
    }


    public static void f2() {
        int x = 15;
        // 1111
        System.out.println(Integer.toBinaryString(x));
        // -16
        System.out.println(~15);

        /**
         * 　负数原码转化为补码：符号位不变，数值位按位取反，末尾加一。
         *   原码 1100 0010
         *   反码 1011 1101 //符号位不变，数值位按位取反
         *   补码 1011 1110 //末尾加1
         */
        // 11111111111111111111111111110000 (补码)
        System.out.println(Integer.toBinaryString(~15));
        System.out.println(Integer.toBinaryString(-16));

        System.out.println("============100===========");

        System.out.println(Integer.toBinaryString(100));
        System.out.println((100 & ~15));
    }
}
