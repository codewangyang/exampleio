package com.example.v1.io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/1 -22:40
 */
public class BIOClient {

    public static void main(String[] args) throws IOException {
        //要和谁进行通信，服务ip 端口号
        //一台机器的端口号是有限的 65535
        Socket socket = new Socket("localhost",8080);

//        输入 流 wirte
//        不管是客户端还是服务端 都有可能write和read
        OutputStream os = socket.getOutputStream();
        String name = UUID.randomUUID().toString();
        System.out.println("客户端发送数据："+name);
        //传说中的101011000
        os.write(name.getBytes());
        os.flush();
        os.close();
        socket.close();
    }
}
