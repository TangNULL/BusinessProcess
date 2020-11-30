package com.example.demo.service;

import com.example.demo.entity.Block;
import com.example.demo.entity.Transaction;

import java.util.List;


/**
 * 生成新区块，验证区块的合法性，保存交易池
 */
public interface BlockService {

    String createGenesisBlock();  //创世区块
    String createNewBlock();  //挖掘节点生成新的区块
    boolean addBlock(Block curBlock);  //添加新区块

    boolean isValidChain(List<Block> blockchain);  //验证区块链的有效性
    void replaceChain(List<Block> blockchain);  //替换本地区块链

    void addTxCache(List<Transaction> txs);  //添加交易缓存
    void removeTxCache(List<Transaction> txs); //更新当前节点的交易池记录
}
