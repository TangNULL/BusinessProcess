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
}
