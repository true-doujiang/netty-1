package com.yhh.unsafe.dto;

public class Hello {

    public int value;
    static public int staticNumber = 11;
    public String name;

    public Hello() {
        System.out.println(" 默认构造器" );
    }

    public Hello(String name) {
        System.out.println("name = " + name );
        this.name = name;
    }

}