package com.example.demo.service;

import org.java_websocket.WebSocket;

import java.util.List;

public interface WebSocketService {
    void write(WebSocket ws, String msg);  //消息传递
    void broadcast(String msg, List<WebSocket> sockets);  //消息广播
    void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets);  //p2p网络的消息处理
}
