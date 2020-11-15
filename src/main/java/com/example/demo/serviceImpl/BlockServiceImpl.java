package com.example.demo.serviceImpl;

import com.example.demo.entity.Block;
import com.example.demo.service.BlockService;
import org.springframework.stereotype.Service;

@Service
public class BlockServiceImpl implements BlockService {
    @Override
    public String createGenesisBlock() {
        return null;
    }

    @Override
    public String createNewBlock() {
        return null;
    }

    @Override
    public boolean isValidBlock(Block currentBlock) {
        return false;
    }

    @Override
    public boolean addBlock(Block currentBlock) {
        return false;
    }

    @Override
    public String getBlockChain() {
        return null;
    }
}
