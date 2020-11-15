package com.example.demo.service;

import com.example.demo.entity.Block;
import com.example.demo.entity.BlockChain;
import org.springframework.stereotype.Service;

/**
 * 生成新区块
 */
public interface BlockService {
    String createGenesisBlock();  // 创世区块
    String createNewBlock();  //生成新的区块
    boolean isValidBlock(Block currentBlock);  //验证区块的合法性
    boolean addBlock(Block currentBlock);  //添加新区块
    String getBlockChain();
}
