package io.netty.buffer;

import java.io.*;
import java.util.Arrays;

public class FileTest {

    public static void main(String[] args) throws Exception {
        String s = "aaaaaaaa土豪就是我";
        byte[] bs= s.getBytes();
        OutputStream out = new FileOutputStream("D:\\log/bbb.txt");
        InputStream is = new ByteArrayInputStream(bs);
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=is.read(buff))!=-1){
            System.out.println(Arrays.toString(buff));
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
    }
}
