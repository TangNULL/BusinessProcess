package com.example.demo.websocket;

import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.service.PublicBlockchainService;
import com.example.demo.service.WebSocketService;
import com.example.demo.serviceImpl.PublicBlockchainServiceImpl;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class NodeServer {

    @Autowired
    LocalPublicBlockchain localBlockChain;

    @Autowired
    WebSocketService webSocketService;


    public void init(int port) {
        WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {

            /**
             * 连接建立后触发
             */
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                localBlockChain.getSockets().add(webSocket);
                System.out.println("客户端连入: " + webSocket.getRemoteSocketAddress());
            }

            /**
             * 连接关闭后触发
             */
            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                localBlockChain.getSockets().remove(webSocket);
                System.out.println("客户端关闭: " + webSocket.getRemoteSocketAddress());
            }

            /**
             * 接收到客户端消息时触发
             */
            @Override
            public void onMessage(WebSocket webSocket, String msg) {
                //作为服务端，业务逻辑处理
                webSocketService.handleMessage(webSocket, msg, localBlockChain.getSockets());
//                System.out.println("连接的websocket数量: " + localBlockChain.getSockets().size());
//                for (WebSocket ws : localBlockChain.getSockets()) {
//                    System.out.println("远程地址为: " + ws.getRemoteSocketAddress().getHostString());
//                }
//                for (WebSocket ws : localBlockChain.getSockets()) {
//                    System.out.println("本地地址为: " + ws.getLocalSocketAddress().getHostString());
//                }
//                for (WebSocket ws : localBlockChain.getSockets()) {
//                    System.out.println("websocket为: " + ws.toString());
//                }
            }

            /**
             * 发生错误时触发
             */
            @Override
            public void onError(WebSocket webSocket, Exception e) {
                localBlockChain.getSockets().remove(webSocket);
                System.out.println("客户端连接错误: " + webSocket.getRemoteSocketAddress());
            }

            @Override
            public void onStart() {

            }

        };
        System.out.println("连接的websocket数量: " + localBlockChain.getSockets().size());
        socketServer.start();
        System.out.println("监听端口: " + port);
    }
}
