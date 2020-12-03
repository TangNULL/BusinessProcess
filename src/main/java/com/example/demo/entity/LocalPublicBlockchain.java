package com.example.demo.entity;

import org.java_websocket.WebSocket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 当前节点保存的公有链相关的数据
 */

@Component
public class LocalPublicBlockchain {
    private List<Block> blockChain = new ArrayList<>();  //本地节点上保存的区块信息
    private List<Transaction> txCache = new ArrayList<>();  //本地节点上接收到的交易池信息
    private List<User> users = new ArrayList<>();  //本地节点上保存的用户全局状态
    private List<WebSocket> sockets = new ArrayList<>();  //本地节点上的邻居节点
    private String address;  //本机ip地址
    private int port;  //本地端口号


    public List<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        this.blockChain = blockChain;
    }

    public List<Transaction> getTxCache() {
        return txCache;
    }

    public void setTxCache(List<Transaction> txCache) {
        this.txCache = txCache;
    }

    public List<WebSocket> getSockets() {
        return sockets;
    }

    public void setSockets(List<WebSocket> sockets) {
        this.sockets = sockets;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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

    public Block getLatestBlock() { return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null; }


}
