package com.yhh;

public class TestBinary {

    public static void main(String[] args) {
        int x = 0xFFFFFE00;
        //-512
        System.out.println(x);
        //11111111111111111111111000000000
        System.out.println(Integer.toBinaryString(x));

        int y = 512;
        //1000000000
        System.out.println(Integer.toBinaryString(y));

        boolean isLess512 = (511 & 0xFFFFFE00) == 0;
        System.out.println("isLess512: " + isLess512);

        System.out.println("---------------");

        int z = 1;
        z = z<<9;
        System.out.println(z);
        System.out.println(Integer.toBinaryString(z));
    }
}
