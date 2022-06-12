package com.example.v1.netty.tomcat.servlet;

import com.example.v1.netty.tomcat.http.NRequest;
import com.example.v1.netty.tomcat.http.NResponse;
import com.example.v1.netty.tomcat.http.NServlet;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/3 -22:32
 */
public class FirstServlet extends NServlet {

    @Override
    protected void doPost(NRequest request, NResponse response) {
        response.write("this is first servlet");
    }

    @Override
    protected void doGet(NRequest request, NResponse response) {
         this.doPost(request,response);
    }
}
