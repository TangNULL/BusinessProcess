package com.example.demo.service;

import com.example.demo.entity.PublicBlock;

/**
 * 区块链共识算法
 */
public interface ConsensusService {
    PublicBlock PoWMine();  //pow机制
    PublicBlock PoSMine();  //pos机制
    boolean isValidHash(String hash, int difficulty);  //验证区块的合法性
}
