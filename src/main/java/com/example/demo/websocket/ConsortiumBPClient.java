package com.example.demo.websocket;

import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.service.ConsortiumBlockchainService;
import com.example.demo.service.PublicBlockchainService;
import com.example.demo.service.WebSocketService;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ConsortiumBPClient {

    @Autowired
    ConsortiumBlockchainService consortiumBlockchainService;

    @Autowired
    LocalPublicBlockchain localBlockChain;

    @Autowired
    WebSocketService webSocketService;

    public void init(String serverAddr, String senderMsg) {
        try {
            final WebSocketClient socketClient = new WebSocketClient(new URI(serverAddr)) {
                /**
                 * 连接建立后触发
                 */
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    //客户端发送请求，查询最新区块
                    webSocketService.write(this, senderMsg);
                    System.out.println("向协作节点发起合作请求");
                }

                /**
                 * 连接关闭后触发
                 */
                @Override
                public void onClose(int i, String msg, boolean b) {
                    System.out.println("关闭和协作节点的连接");
                }

                /**
                 * 接收到消息时触发
                 * @param msg
                 */
                @Override
                public void onMessage(String msg) {
                    // 收到返回的确认通知
                }

                /**
                 * 发生错误时触发
                 */
                @Override
                public void onError(Exception e) {
                    System.out.println("连接协作节点失败！");
                }
            };
            socketClient.connect();
        } catch (URISyntaxException e) {
            System.out.println("连接错误: " + e.getMessage());
        }
    }
}
