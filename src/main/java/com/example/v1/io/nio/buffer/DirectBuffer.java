package com.example.v1.io.nio.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:直接缓存区
 * @USER: wangy
 * @DATE: 2021/11/3 -18:41
 */
public class DirectBuffer {
    public static void main(String[] args) throws IOException {
        //首先我们在从磁盘上读取刚才我们写出的文件内容
        String infile = "D://test.txt";
        FileInputStream fin = new FileInputStream(infile);
        FileChannel fcin = fin.getChannel();

        //把我们刚刚读取的内容写入到一个新的文本中
        String outfile =  String.format("D://testcopy.txt");
        FileOutputStream fon = new FileOutputStream(outfile);
        FileChannel fcon = fon.getChannel();
        //使用是allocateDirect  而不是allocate
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while (true){
            buffer.clear();
            int r = fcin.read(buffer);
            if (r==-1){
                break;
            }
            buffer.flip();
            fcon.write(buffer);
        }
    }
}
