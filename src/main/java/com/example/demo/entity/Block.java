package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 区块block的数据结构
 */
public class Block extends lightBlock   {

    private List<Transaction> txs;  //当前区块包含的业务流程
    private List<User> users;  //当前区块包含的用户信息
    private List<Data> data;  //当前区块包含的用户数据

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
