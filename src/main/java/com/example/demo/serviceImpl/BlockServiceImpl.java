package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Block;
import com.example.demo.entity.BlockChain;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.service.BlockService;
import com.example.demo.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlockServiceImpl implements BlockService {

    BlockChain blockChain = new BlockChain();

    @Override
    public String createGenesisBlock() {
        Block genesisBlock = new Block();
        // 创世区块属性
        genesisBlock.setBlockId(1);
        genesisBlock.setTimestamp(System.currentTimeMillis());
        genesisBlock.setNonce(1);
        genesisBlock.setPreHash("");

        List<User> userList = new ArrayList<User>();
        userList.add(new User("u1", "123456", "u1", "des", "core", "ass"));
        userList.add(new User("u2", "123456", "u1", "des", "core", "ass"));
        genesisBlock.setUsers(userList);
        genesisBlock.setHash(CryptoUtil.calcBlockHash(genesisBlock));
        //添加到区块链中
        blockChain.getBlockChain().add(genesisBlock);
        return JSON.toJSONString(genesisBlock);
    }

    @Override
    public String createNewBlock() {
        ConsensusServiceImpl consensusServiceImpl = new ConsensusServiceImpl();
        Block newBlock = consensusServiceImpl.PoWMine(blockChain);
        if (addBlock(newBlock)) {
            return JSON.toJSONString(newBlock);
        }
        else {
            return "Error!";
        }

    }

    @Override
    public boolean addBlock(Block newBlock) {
        //先对新区块的合法性进行校验
        if (isValidBlock(blockChain.getLatestBlock(), newBlock)) {
            blockChain.getBlockChain().add(newBlock);
            return true;
        }
        return false;
    }

    @Override
    // 这里最简单的判断条件，之后会添加完整
    public boolean isValidBlock(Block preBlock, Block curBlock) {
        if(preBlock.getHash().equals(curBlock.getPreHash())){
            return true;
        }
        return false;
    }

    @Override
    public String getBlockChain() {
        return JSON.toJSONString(blockChain.getBlockChain());
    }

    @Override
    public void updateTxCache(Block newBlock){
        blockChain.getTxCache().removeAll(newBlock.getTxs());
    }



}
