package com.example.demo.service;

import com.example.demo.entity.Block;

import java.util.List;


/**
 * 生成新区块
 */
public interface BlockService {

    String createGenesisBlock();  //创世区块
    String createNewBlock();  //挖掘节点生成新的区块
    boolean addBlock(Block currentBlock);  //添加新区块
    boolean isValidBlock(Block preBlock, Block curBlock);  //验证区块的合法性
    String getBlockChain();//获取当前节点的区块链
//    boolean isValidChain(List<Block> blockchain);  //验证区块链的有效性
//    void replaceChain(List<Block> blockchain);  //替换本地区块链
    void removeTxCache(Block newBlock); //更新当前节点的交易池记录
}
