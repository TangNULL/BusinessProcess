package com.example.demo.service;

import com.example.demo.entity.Block;


/**
 * 生成新区块
 */
public interface BlockService {

    String createGenesisBlock();  //创世区块
    String createNewBlock();  //挖掘节点生成新的区块
    //其他节点
    boolean addBlock(Block currentBlock);  //添加新区块
    boolean isValidBlock(Block preBlock, Block curBlock);  //验证区块的合法性
    String getBlockChain();//获取当前节点的区块链
    public void updateTxCache(Block newBlock); //更新当前节点的交易池记录
}
