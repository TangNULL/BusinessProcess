package com.example.demo.entity;

import org.java_websocket.WebSocket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 当前节点保存的联盟链数据
 */
@Component
public class LocalConsortiumBlockchain {
    private List<Transaction> txs;  //本地节点上保存的交易信息
    private List<User> users;  //本地节点保存的用户信息
    private Map<String, String> pkHash;  //用户私钥的哈希值
    private String bpId;  //标识唯一流程实例
    private String address;  //本机ip地址
    private int port;  //本地端口号
    private List<WebSocket> sockets = new ArrayList<>();  //本地节点上的邻居节点

    public List<Transaction> getTxs() {
        return txs;
    }

    public void setTxs(List<Transaction> txs) {
        this.txs = txs;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Map<String, String> getPkHash() {
        return pkHash;
    }

    public void setPkHash(Map<String, String> pkHash) {
        this.pkHash = pkHash;
    }

    public String getBpId() {
        return bpId;
    }

    public void setBpId(String bpId) {
        this.bpId = bpId;
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

    public List<WebSocket> getSockets() {
        return sockets;
    }

    public void setSockets(List<WebSocket> sockets) {
        this.sockets = sockets;
    }
}
