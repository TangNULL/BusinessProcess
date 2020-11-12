package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存所有区块的数据
 */
public class BlockChain {
    private List<Block> blockChain = new ArrayList<>();  //节点上保存的区块信息

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        this.blockChain = blockChain;
    }
}
