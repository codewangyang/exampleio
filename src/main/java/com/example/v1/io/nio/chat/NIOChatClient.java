package com.example.v1.io.nio.chat;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.chrono.IsoChronology;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/3 -19:50
 */
public class NIOChatClient {

    private final InetSocketAddress socketAddress = new InetSocketAddress("localhost",8088);

    private Selector selector;

    private SocketChannel client;

    private String nickName;

    private Charset charset = Charset.forName("UTF-8");

    private static String USER_EXIST = "系统提示：改昵称已经存在,请换一个昵称";

    private static String USER_CONTENT_SPILIT = "#@#";

    public NIOChatClient() throws IOException {
        selector = Selector.open();

        client = SocketChannel.open(socketAddress);
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    public void session(){
        //开辟一个新线程从服务器读取数据
        new Reader().start();
        //开辟一个新线程往服务器写数据
        new Writer().start();
    }

   private class Reader extends Thread{

       @Override
       public void run() {
          try {
              while (true) {
                  int readrChannels = selector.select();
                  if (readrChannels==0)continue;
                  Set<SelectionKey> selectionKeys  = selector.selectedKeys();
                  Iterator<SelectionKey> itor = selectionKeys.iterator();
                  while (itor.hasNext()){
                      SelectionKey key = (SelectionKey) itor.next();
                      itor.remove();
                      process(key);
                  }

              }
          } catch (IOException e) {
              e.printStackTrace();
          }

       }
   }



    private class Writer extends Thread{

        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNext()){
                    String line = scanner.next();
                    if ("".equals(line))continue;
                    if ("".equals(nickName)){
                        nickName = line ;
                        line = nickName + USER_CONTENT_SPILIT;

                    }else{
                        line = nickName+USER_CONTENT_SPILIT+line;
                    }
                    client.write(charset.encode(line));//client 既能写也能读
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuffer content = new StringBuffer();

            while (channel.read(buffer) > 0) {
                buffer.flip();
                content.append(charset.decode(buffer));
            }
            if (USER_EXIST.equals(content)){
                nickName = "";
            }
            System.out.println(content);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOChatClient().session();
    }
}
