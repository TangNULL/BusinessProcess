package com.example.demo.entity;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<WebSocket> sockets = new ArrayList<>();  //本地节点上的邻居节点
    private String address;  //本机ip地址
    private int port;  //本地端口号

    public List<WebSocket> getSockets() {
        return sockets;
    }

    public void setSockets(List<WebSocket> sockets) {
        this.sockets = sockets;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
