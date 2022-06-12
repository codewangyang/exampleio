package com.example.v1.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:同步阻塞IO模型
 * @USER: wangy
 * @DATE: 2021/11/1 -22:13
 */
public class BIOServer {

    //服务端网络io模型的封装对象
    ServerSocket server;

    public BIOServer(int port) {
        //tomcat 默认端口号是8080
//        只要是java写的都是这么玩的


        try {
            this.server = new ServerSocket(port);
            System.out.println("BIO服务已启动，监听端口是："+port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    * 开始监听  并处理逻辑
    *
    * */
   public void listen()throws IOException{
           while (true){
               //等待客户端链接  阻塞方法
               //Socket数据发生者在服务端的引用
               Socket client = server.accept();
               System.out.println(client.getPort());
               //对方的数据给我了  读取input
               InputStream is = client.getInputStream();

               //网络客户端的数据发送到网卡  机器所得到的数据读到JVM中
               byte [] buff = new byte[1024];
               int len = 0;
               if ((len = is.read(buff))>0){
                    String msg = new String(buff,0,len);
                   System.out.println("收到:"+msg);
               }
           }
   }

    public static void main(String[] args) throws IOException {
        new BIOServer(8080).listen();
    }
}
