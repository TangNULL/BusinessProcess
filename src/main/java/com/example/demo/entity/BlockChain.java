package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存所有区块的数据
 */
public class BlockChain {
    private List<Block> blockChain = new ArrayList<>();  //本地节点上保存的区块信息
    private List<Transaction> txCache = new ArrayList<>();  //本地节点上接收到的交易池信息

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


}
