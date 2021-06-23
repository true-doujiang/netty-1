package com.yhh.moni2.csdn;


interface Parent {

	public static final int a = 1;

	public static Thread thread = new Thread("t1"){
		{
			System.out.println("aaaaaaaaaa:" + (a));
		}
	};

	public static Thread thread2 = new Thread("t2"){
		{
			System.out.println("bbbbbbb:" + (a + 1));
		}
	};

}


public class InterfaceTest {

	public static void main(String[] args) {
		// 为毛 thread2也打印了
		System.out.println(Parent.thread);
//		System.out.println("Parent.a = " + Parent.a);
	}
}
