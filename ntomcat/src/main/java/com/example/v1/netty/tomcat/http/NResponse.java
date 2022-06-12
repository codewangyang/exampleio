package com.example.v1.netty.tomcat.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/3 -22:35
 */
public class NResponse {

    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public NResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out){
        try {
            if (out == null || out.length() ==0){
                return;
            }
            FullHttpResponse response = new DefaultFullHttpResponse(
                    //设置http版本为1.1
                    HttpVersion.HTTP_1_1,
                    //设置响应状态码
                    HttpResponseStatus.OK,
                    //将输出值写出  编码为utf-8
                    Unpooled.wrappedBuffer(out.getBytes(StandardCharsets.UTF_8))
            );
            response.headers().set("Content-Type","text/html");
            ctx.write(response);
        } finally {
           ctx.flush();
           ctx.close();
        }

    }
}
