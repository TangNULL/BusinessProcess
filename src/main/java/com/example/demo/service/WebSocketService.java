package com.example.demo.service;

import org.java_websocket.WebSocket;

import java.util.List;

public interface WebSocketService {
    void broadcast(String message);  //消息广播
    void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets);  //消息处理
    void handleBlockResponse(String blockData, List<WebSocket> sockets);  //处理区块消息
    void handleBlockChainResponse(String blockData, List<WebSocket> sockets);  //处理区块链消息
}
