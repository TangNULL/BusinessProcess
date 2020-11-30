package com.example.demo.service;

import com.example.demo.entity.Block;

/**
 * 区块链共识算法
 */
public interface ConsensusService {
    Block PoWMine();  //pow机制
    Block PoSMine();  //pos机制
    boolean isValidHash(String hash, int difficulty);  //验证区块的合法性
}
