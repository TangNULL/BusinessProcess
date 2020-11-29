package com.example.demo.websocket;

import com.example.demo.serviceImpl.WebSocketServiceImpl;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

public class NodeServer {

//    WebSocketServiceImpl webSocketServiceImpl;

    public void init(int port) {
        WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {

            /**
             * 连接建立后触发
             */
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
//                webSocketServiceImpl.getSockets().add(webSocket);
                System.out.println("connection start to address:" + webSocket.getRemoteSocketAddress());
            }

            /**
             * 连接关闭后触发
             */
            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
//                webSocketServiceImpl.getSockets().remove(webSocket);
                System.out.println("connection closed to address:" + webSocket.getRemoteSocketAddress());
            }

            /**
             * 接收到客户端消息时触发
             */
            @Override
            public void onMessage(WebSocket webSocket, String msg) {
//                //作为服务端，业务逻辑处理
//                webSocketServiceImpl.handleMessage(webSocket, msg, webSocketServiceImpl.getSockets());
            }

            /**
             * 发生错误时触发
             */
            @Override
            public void onError(WebSocket webSocket, Exception e) {
//                webSocketServiceImpl.getSockets().remove(webSocket);
//                System.out.println("connection failed to address:" + webSocket.getRemoteSocketAddress());
            }

            @Override
            public void onStart() {

            }

        };
        socketServer.start();
        System.out.println("listening websocket p2p port on: " + port);
    }
}
