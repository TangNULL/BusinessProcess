package com.example.demo.websocket;

import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.service.PublicBlockchainService;
import com.example.demo.service.WebSocketService;
import com.example.demo.serviceImpl.PublicBlockchainServiceImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class NodeClient {

    @Autowired
    PublicBlockchainService publicBlockchainService;

    @Autowired
    LocalPublicBlockchain localBlockChain;

    @Autowired
    WebSocketService webSocketService;

    public void init(String serverAddr) {
        try {
            final WebSocketClient socketClient = new WebSocketClient(new URI(serverAddr)) {
                /**
                 * 连接建立后触发
                 */
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    //客户端发送请求，查询最新区块
                    webSocketService.write(this, publicBlockchainService.queryLatestBlockMsg());
                    localBlockChain.getSockets().add(this);
                    System.out.println("向邻居节点查询最新区块");
                }

                /**
                 * 连接关闭后触发
                 */
                @Override
                public void onClose(int i, String msg, boolean b) {
                    localBlockChain.getSockets().remove(this);
                    System.out.println("关闭和邻居节点的连接");
                }

                /**
                 * 接收到消息时触发
                 * @param msg
                 */
                @Override
                public void onMessage(String msg) {
                    webSocketService.handleMessage(this, msg, localBlockChain.getSockets());
                }

                /**
                 * 发生错误时触发
                 */
                @Override
                public void onError(Exception e) {
                    localBlockChain.getSockets().remove(this);
                    System.out.println("服务器连接失败！");
                }
            };
            socketClient.connect();
        } catch (URISyntaxException e) {
            System.out.println("连接错误: " + e.getMessage());
        }
    }
}
