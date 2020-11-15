package com.example.demo.entity;

/**
 * 轻节点上的区块，只包含区块头信息
 */
public class lightBlock {
    private Integer blockId;  //区块高度
    private String hash;  //当前区块hash值
    private String preHash;  //前一个区块hash值
    private long timestamp;  //生成当前区块时间戳

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
