package com.yhh.example.ch7;

public class MemoryMapTest {


    public static void main(String[] args) {

        //System.out.println(Integer.toBinaryString(4095));

        int maxOrder = 11;
        final byte[] memoryMap;
        final byte[] depthMap;

        final int maxSubpageAllocs;
        // 2^11 = 2048
        maxSubpageAllocs = 1 << maxOrder;

        // 4096长度
        memoryMap = new byte[maxSubpageAllocs << 1];
        depthMap = new byte[memoryMap.length];

        //从1开始
        int memoryMapIndex = 1;

        for (int d = 0; d <= maxOrder; ++ d) { // move down the tree one level at a time
            int depth = 1 << d;
            for (int p = 0; p < depth; ++ p) {
                // in each level traverse left to right and set value to the depth of subtree
                memoryMap[memoryMapIndex] = (byte) d;
                depthMap[memoryMapIndex] = (byte) d;
                System.out.println("memoryMapIndex = " + memoryMapIndex);
                memoryMapIndex ++;
            }
        }

        System.out.println("memoryMapIndex = " + memoryMapIndex);
        System.out.println("memoryMap = " + memoryMap);
        System.out.println("depthMap = " + depthMap);
    }
}
