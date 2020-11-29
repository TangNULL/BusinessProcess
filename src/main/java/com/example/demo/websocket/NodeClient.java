package com.example.demo.websocket;

import com.example.demo.serviceImpl.WebSocketServiceImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class NodeClient {
//    WebSocketServiceImpl webSocketServiceImpl;

    public void init(String serverAddr) {
        try {
            final WebSocketClient socketClient = new WebSocketClient(new URI(serverAddr)) {
                /**
                 * 连接建立后触发
                 */
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
//                    //客户端发送请求，查询最新区块
//                    webSocketServiceImpl.write(this, webSocketServiceImpl.queryLatestBlockMsg());
//                    webSocketServiceImpl.getSockets().add(this);
                    System.out.println("connection open");
                }

                /**
                 * 连接关闭后触发
                 */
                @Override
                public void onClose(int i, String msg, boolean b) {
//                    webSocketServiceImpl.getSockets().remove(this);
                    System.out.println("connection closed");
                }

                /**
                 * 接收到消息时触发
                 * @param msg
                 */
                @Override
                public void onMessage(String msg) {
//                    webSocketServiceImpl.handleMessage(this, msg, webSocketServiceImpl.getSockets());
                }

                /**
                 * 发生错误时触发
                 */
                @Override
                public void onError(Exception e) {
//                    webSocketServiceImpl.getSockets().remove(this);
                    System.out.println("Client Error! Connection failed");
                }
            };
            socketClient.connect();
        } catch (URISyntaxException e) {
            System.out.println("p2p connect is error:" + e.getMessage());
        }
    }
}
