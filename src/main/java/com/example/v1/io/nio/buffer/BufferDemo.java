package com.example.v1.io.nio.buffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:缓冲区
 * @USER: wangy
 * @DATE: 2021/11/1 -22:56
 */
public class BufferDemo {
    public static void main(String[] args) throws IOException {
        //这里用的是文件的io操作
        FileInputStream fin= new FileInputStream("D://test.txt");
        //创建文件的操作管道
        FileChannel fc = fin.getChannel();
        //分配一个10个大小缓冲区 说白了 就是分配一个10个大小的byte数组
        ByteBuffer buffer = ByteBuffer.allocate(10);
        output("初始化",buffer);

//        先读一下
        fc.read(buffer);
        output("调用read()",buffer);
//        准备操作之前 先锁定操作范围
        buffer.flip();
        output("调用flip",buffer);

//        判断有么有可读数据
          while (buffer.remaining()>0){
              byte b =buffer.get();
//              System.out.println("ch");
          }
          output("调用get()",buffer);

          //可以理解为解锁
        buffer.clear();
        output("调用clear",buffer);

        fin.close();

    }

    /*把这个缓冲区里面实时状态给答案出来*/
    private static void output(String step,ByteBuffer buffer) {
        System.out.println(step+":");
        //容量，数组大小
        System.out.println("capacity:"+ buffer.capacity()+",");
        //当前操作数据所在的位置，也可以叫做游标
        System.out.println("position"+ buffer.position()+",");
//         锁定值,flip,数据操作范围索引只能在position - limit 之间
        System.out.println("limit"+ buffer.limit());
        System.out.println();
    }

}
