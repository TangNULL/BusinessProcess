package com.example.demo.serviceImpl;

import com.example.demo.entity.Block;
import com.example.demo.service.ConsensusService;

public class ConsensusServiceImpl implements ConsensusService {

    @Override
    public Block mine() {
        return null;
    }

    @Override
    public boolean isValid(String hash) {
        return false;
    }
}
