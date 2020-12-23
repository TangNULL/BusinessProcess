package com.example.demo.service;

import com.example.demo.entity.PublicBlock;

import java.util.List;


/**
 * 生成新区块，验证区块的合法性，保存交易池
 */
public interface BlockService {

    String createGenesisBlock();  //创世区块

    String createNewBlock();  //挖掘节点生成新的区块

    boolean addBlock(PublicBlock curBlock);  //添加新区块

    boolean isValidChain(List<PublicBlock> blockchain);  //验证区块链的有效性

    void replaceChain(List<PublicBlock> blockchain);  //替换本地区块链
}
