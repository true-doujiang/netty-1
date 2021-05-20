package com.yhh.unsafe.dto;

import com.yhh.unsafe.dto.Hello;

public class UnsafeTestKlass {

    public int value;
    static public int staticNumber = 5;
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