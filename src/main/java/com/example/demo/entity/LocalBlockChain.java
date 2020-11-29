package com.example.demo.entity;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 当前节点保存的区块相关的数据
 */
public class LocalBlockChain {
    private List<Block> blockChain = new ArrayList<>();  //本地节点上保存的区块信息
    private List<Transaction> txCache = new ArrayList<>();  //本地节点上接收到的交易池信息
    private List<WebSocket> sockets = new ArrayList<>();  //本地节点上的邻居节点
    private String address;  //本机ip地址
    private int port;  //本地端口号
    private int difficulty;  //区块链难度系数


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

    public Block getLatestBlock() { return blockChain.get(blockChain.size()-1); }

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
