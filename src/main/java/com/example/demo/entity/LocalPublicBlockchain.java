package com.example.demo.entity;

import org.java_websocket.WebSocket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 当前节点保存的公有链相关的数据
 */

@Component
public class LocalPublicBlockchain {
    private List<PublicBlock> blockChain = new ArrayList<>();  //本地节点上保存的区块信息
    private Set<Transaction> txCache = new HashSet<>();  //本地节点上接收到的交易池信息
    private List<User> users = new ArrayList<>();  //本地节点上保存的用户全局状态
    private List<WebSocket> sockets = new ArrayList<>();  //本地节点上的邻居节点
    private String address;  //本机ip地址
    private int port;  //本地端口号

    public List<PublicBlock> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<PublicBlock> blockChain) {
        this.blockChain = blockChain;
    }

    public Set<Transaction> getTxCache() {
        return txCache;
    }

    public void setTxCache(Set<Transaction> txCache) {
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

    //获取最新区块
    public PublicBlock getLatestBlock() { return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null; }

    //添加交易缓存
    public void addTxCache(Set<Transaction> txs) {
        txCache.addAll(txs);
    }

    //更新当前节点的交易池记录
    public void removeTxCache(List<Transaction> txs){
        txCache.removeAll(txs);
    }

    //添加新用户
    public void addUser(User newUser) {
        users.add(newUser);
    }
}
