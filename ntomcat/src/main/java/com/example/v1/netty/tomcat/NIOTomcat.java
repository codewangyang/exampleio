package com.example.v1.netty.tomcat;

import com.example.v1.netty.tomcat.http.NRequest;
import com.example.v1.netty.tomcat.http.NResponse;
import com.example.v1.netty.tomcat.http.NServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/3 -23:05
 */
public class NIOTomcat {
    //打开Tomcat源码 全局搜索ServerSocket
    private int port = 8080;

    private Map<String, NServlet> servletMapping = new HashMap<>();

    private Properties webxml = new Properties();

    public void init(){
        //加载web.xml文件 同时初始化ServletMapping对象

        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fin = new FileInputStream(WEB_INF+"web.properties");
            webxml.load(fin);

            for (Object k:webxml.keySet()){
                String key = k.toString();
                if (key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$","");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName+".className");
                    NServlet nServlet = (NServlet) Class.forName(className).newInstance();
                    servletMapping.put(url,nServlet);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
     public void start(){
        init();
        //netty封装了nio reactor模式  boss worker
         //boss线程
         EventLoopGroup bossGrop = new NioEventLoopGroup();
         //worker现场
         EventLoopGroup workerGroup = new NioEventLoopGroup();

         try {
             //netty 服务
             //servet b
             ServerBootstrap server= new ServerBootstrap();
             //链路式编程
             server.group(bossGrop,workerGroup)
                     //主程序处理类  看到这样的写法 底层是反射
                     .channel(NioServerSocketChannel.class)
                     //子线程处理类  Handler
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         //客户端初始化处理
                                       @Override
                                       protected void initChannel(SocketChannel client) throws Exception {
                                           //无锁化串行编程
                                           //netty对http协议的封装  顺序有要求
                                           //HttpRequestEncoder编码器
                                           client.pipeline().addLast(new HttpRequestEncoder());
                                           //HttpRequestDecoder 解码器
                                           client.pipeline().addLast(new HttpRequestDecoder());
                                           //业务逻辑处理
                                           client.pipeline().addLast(new NIOTomcatHandler());
                                       }

          //针对主线程的配置  分配线程最大数量128   //针对子线程的配置  保持长连接
         }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);

             ChannelFuture f = server.bind(port).sync();
             System.out.println("Tomcat 已启动  监听端口是："+port);
             f.channel().closeFuture().sync();
         } catch (InterruptedException e) {
             e.printStackTrace();
         }finally {
             bossGrop.shutdownGracefully();
             workerGroup.shutdownGracefully();
         }
     }

    private class NIOTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
             if (msg instanceof HttpRequest){
                 HttpRequest req = (HttpRequest) msg;

                 //转交给我们自己的request实现
                 NRequest request = new NRequest(ctx,req);
                 //转给我们自己的response实现
                 NResponse response = new NResponse(ctx,req);

                 String url = request.getUrl();
                 if (servletMapping.containsKey(url)){
                     servletMapping.get(url).service(request,response);
                 }else{
                     response.write("404 - not found");
                 }

             }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }

    public static void main(String[] args) {
        new NIOTomcat().start();
    }
}
