package com.example.demo.service;

import com.example.demo.entity.Block;

public interface ConsensusService {
    Block mine();
    boolean isValid(String hash);  //验证区块的合法性
}
