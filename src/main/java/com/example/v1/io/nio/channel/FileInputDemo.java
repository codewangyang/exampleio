package com.example.v1.io.nio.channel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/1 -23:36
 */
public class FileInputDemo {
    public static void main(String[] args) throws IOException {
        FileInputStream fin = new FileInputStream("D://test.txt");

        FileChannel fc = fin.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        fc.read(buffer);
        buffer.flip();
        while (buffer.remaining()>0){
            byte  b =buffer.get();
            System.out.println((char)b);
        }
        fin.close();
    }
}
