package com.example.demo.entity;

import java.io.Serializable;

/**
 * 轻节点上的区块，只包含区块头信息
 */
public class LightBlock implements Serializable {
    private static final long serialVersionUID = 1L;
    private int blockId;  //区块高度
    private long timestamp;  //生成当前区块时间戳
    private String target;  //区块生成的难度值
    private int nonce;  //计算随机数
    private String preHash;  //前一个区块hash值
    private String hash;  //当前区块hash值


    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getNonce() { return nonce; }

    public void setNonce(int nonce) { this.nonce = nonce; }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }




}
