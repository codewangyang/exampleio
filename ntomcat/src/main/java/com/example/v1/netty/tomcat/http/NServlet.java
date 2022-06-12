package com.example.v1.netty.tomcat.http;

/**
 * @PROJECT_NAME: exampleio
 * @DESCRIPTION:
 * @USER: wangy
 * @DATE: 2021/11/3 -22:33
 */
public abstract class NServlet {
    public void service(NRequest request,NResponse response){
         //由service方法来决定  是调用doGet或者是调用doPost
        if ("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else{
            doPost(request,response);
        }
        
    }

    protected abstract void doPost(NRequest request, NResponse response);

    protected abstract void doGet(NRequest request, NResponse response);
}
