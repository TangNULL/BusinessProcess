package com.example.demo.service;

import com.example.demo.entity.Block;
import com.example.demo.entity.LocalBlockChain;

public interface ConsensusService {
    Block PoWMine(LocalBlockChain blockChain);  //pow机制
    boolean isValidHash(String hash, String target);  //验证区块的合法性
}
