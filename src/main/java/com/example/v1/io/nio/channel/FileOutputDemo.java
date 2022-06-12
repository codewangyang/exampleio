package com.example.v1.io.nio.channel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/1 -23:42
 */
public class FileOutputDemo {
    static private final byte message[] = { 83, 111, 109, 101, 32, 98, 121, 116, 101, 115, 46 };

    public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream("D://test.txt");
        FileChannel fc = fos.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < message.length; i++) {
            buffer.put(message[i]);
        }

        buffer.flip();
        fc.write(buffer);
        fc.close();
    }
}
