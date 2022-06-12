package com.example.v1.io.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * 网络多功能聊天室
 *   功能1：客户端通过Java NIO 连接到服务器  支付多客户端的连接
 *   功能2： 客户端初次链接时服务端提示输入昵称 如果昵称已经有人使用  提示重新输入  如果昵称唯一  则登录成功  之后发生消息都需要安装规定带着昵称发送消息
 *   功能3：客户端登录后 发送已经设置好的欢迎信息和在线人数给客户端  并且通知其他客户端该客户端上线
 *   功能4： 服务器收到已登录客户端内容  转发至其他登录客户端
 * @USER: wangy
 * @DATE: 2021/11/3 -19:51
 */
public class NIOChatServer {
    private int port = 808;

    private Charset charset = Charset.forName("UTF-8");
    //用来记录在线人数以及昵称
    private static HashSet<String> users = new HashSet<>();

    private static String USER_EXIST = "系统提示：改昵称已经存在,请换一个昵称";
    //相当于自定义协议格式  与客户端协商好
    private static String USER_CONTENT_SPILIT = "#@#";

    private Selector selector;


    public NIOChatServer(int port) {
        try {
            this.port = port;
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);
            selector =Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务已启动，监听端口是："+this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen() throws IOException {
        while (true){
            int wait = selector.select();
            if (wait == 0)continue;

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> itor = keys.iterator();
            while (itor.hasNext()){
                SelectionKey key = (SelectionKey) itor.next();
                itor.remove();
                process(key);
            }
         }
    }

    private void process(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            //非阻塞状态
            channel.configureBlocking(false);
            //选择注册器并设置为读取状态  收到一个请求  然后起一个SocketChannel 并注册到selector上
            channel.register(selector,SelectionKey.OP_READ);
            //将此对应的channel 设置成为接受其他客户端的请求
            key.interestOps(SelectionKey.OP_ACCEPT);

            channel.write(charset.encode("请输入你的昵称:"));

        }else if (key.isReadable()){
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer =ByteBuffer.allocate(1024);
            StringBuffer content = new StringBuffer();

            try {
                while (channel.read(buffer)>0){
                    buffer.flip();
                    content.append(charset.decode(buffer));
                }
                key.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                key.cancel();
                if (key.channel()!=null){
                    key.channel().close();
                }
            }

            if (content.length()>0){
                String [] arrayContent = content.toString().split(USER_CONTENT_SPILIT);
                //注册用户
                if (arrayContent!=null &&  arrayContent.length == 1){
                    String nickName = arrayContent[1];
                    if (users.contains(nickName)){
                        channel.write(charset.encode(USER_EXIST));
                    }else{
                        users.add(nickName);
                        int onlineCount = onlieCount();
                        String  message = "欢迎"+nickName+"进入聊天室！当前在线人数："+onlineCount;
                        broadCast(null,message);
                    }
                }
                else if (arrayContent!=null && arrayContent.length>1){
                    String nickName = arrayContent[0];
                    String message = content.substring(nickName.length()+USER_CONTENT_SPILIT.length());
                    message = nickName+"说"+message;
                    if (users.contains(nickName)){
                        //不回发给发送此内容的客户端
                        broadCast(channel,message);
                    }
                }
            }

        }


    }

    private int onlieCount() {
        int res = 0 ;
        for (SelectionKey key : selector.keys()){
            Channel target = key.channel();
            if (target instanceof SocketChannel){
                res ++;
            }
        }
        return res;
    }

   public void broadCast(SocketChannel client,String content) throws IOException {
        for (SelectionKey key :selector.keys()){
            Channel targetchannel = key.channel();
            //如果client不为空 不回发送给此内容的客户端
            if (targetchannel instanceof  SocketChannel && targetchannel!=client){
                SocketChannel target = (SocketChannel) targetchannel;
                target.write(charset.encode(content));
            }
        }
   }

    public static void main(String[] args) throws IOException {
        new NIOChatServer(8088).listen();
    }
}
