package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 区块block的数据结构
 */
public class PublicBlock extends LightBlock {

    private List<Transaction> txs;  //当前区块包含的业务流程
    private List<Data> data;  //当前区块包含的用户数据
    private String usersState;  //状态树包含的user的哈希值

    public PublicBlock() {
    }

    public PublicBlock(int blockId, long timestamp, int difficulty, int nonce, String preHash, String hash, List<Transaction> txs, List<Data> data) {
        super(blockId, timestamp, difficulty, nonce, preHash, hash);
        this.txs = txs;
        this.data = data;
    }

    public List<Transaction> getTxs() {
        return txs;
    }

    public void setTxs(List<Transaction> txs) {
        this.txs = txs;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getUsersState() {
        return usersState;
    }

    public void setUsersState(String usersState) {
        this.usersState = usersState;
    }
}
