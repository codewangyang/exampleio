package com.example.v1.io.nio.buffer;

import java.nio.ByteBuffer;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION: 手动分配缓存区
 * @USER: wangy
 * @DATE: 2021/11/1 -23:30
 */
public class BufferWrap {

    public void myMethod(){
        ByteBuffer  buffer1 = ByteBuffer.allocate(10);

        byte[] array = new byte[10];
        ByteBuffer buffer2 = ByteBuffer.wrap(array);

    }
}
