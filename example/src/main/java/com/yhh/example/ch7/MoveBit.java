package com.yhh.example.ch7;

public class MoveBit {

    public static void main(String[] args) {
        int i = 1;
        System.out.println(i<<10);

        int x = 1024;
        // x>>>10  除以1024
        System.out.println(x>>>10);
        System.out.println(1023>>>10);

        int i1 = smallIdx(2049);
        System.out.println("i1 = " + i1);
    }



    static int smallIdx(int normCapacity) {
        int tableIdx = 0;
        // 除以1024
        int i = normCapacity >>> 10;
        while (i != 0) {
            i >>>= 1;
            tableIdx ++;
        }
        return tableIdx;
    }

}
