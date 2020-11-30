package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.*;
import com.example.demo.service.BlockService;
import com.example.demo.service.ConsensusService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockUtil;
import com.example.demo.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlockServiceImpl implements BlockService {

    @Autowired
    LocalPublicBlockchain blockChain;

    @Autowired
    ConsensusService consensusService;

    @Autowired
    WebSocketService webSocketService;

    @Override
    public String createGenesisBlock() {
        if (blockChain.getBlockChain().size() == 0) {
            Block genesisBlock = new Block();
            // 创世区块属性
            genesisBlock.setBlockId(1);
            genesisBlock.setTimestamp(System.currentTimeMillis());
            genesisBlock.setNonce(1);
            genesisBlock.setPreHash("");
            genesisBlock.setDifficulty(4);

            List<User> userList = new ArrayList<User>();
            userList.add(new User("u1", "123456", "u1", "des", "core", "ass"));
            userList.add(new User("u2", "123456", "u1", "des", "core", "ass"));
            genesisBlock.setUsers(userList);
            genesisBlock.setHash(CryptoUtil.calcBlockHash(genesisBlock));
            //添加到区块链中
            blockChain.getBlockChain().add(genesisBlock);
            //通知其他节点
            createBlockBroadcast(genesisBlock);

            return JSON.toJSONString(genesisBlock);
        }
        return JSON.toJSONString(blockChain.getBlockChain().get(0));
    }

    @Override
    public String createNewBlock() {
        Block newBlock = consensusService.PoWMine();

        if (addBlock(newBlock)) {
            //通知其他节点
            createBlockBroadcast(newBlock);
            return JSON.toJSONString(newBlock);
        }
        else {
            return "Error!";
        }
    }

    private void createBlockBroadcast(Block block) {
        //创建成功后，全网广播出去
        NetworkMsg networkMsg = new NetworkMsg();
        networkMsg.setType(BlockUtil.RESPONSE_LATEST_BLOCK);
        networkMsg.setData(JSON.toJSONString(block));
        webSocketService.broadcast(JSON.toJSONString(networkMsg));
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

    //验证是否为最新区块
    private boolean isValidBlock(Block lastBlock, Block curBlock) {
        if(!lastBlock.getHash().equals(curBlock.getPreHash())){
            System.out.println("新区块的前一个区块哈希值不匹配");
            return false;
        }
        if (!consensusService.isValidHash(curBlock.getHash(), curBlock.getDifficulty())) {
            System.out.println("新区块不满足难度值");
            return false;
        }
        if (!CryptoUtil.calcBlockHash(curBlock).equals(curBlock.getHash())) {
            System.out.println("新区块的哈希值无效");
            return false;
        }
        return true;
    }

    @Override
    public boolean isValidChain(List<Block> otherChain) {
        for(int i = 0; i < otherChain.size()-1; i++) {
            if (!isValidBlock(otherChain.get(i), otherChain.get(i+1))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void replaceChain(List<Block> otherChain) {
        if (otherChain.size() > blockChain.getBlockChain().size() && isValidChain(otherChain)) {
            blockChain.setBlockChain(otherChain);
            blockChain.getTxCache().clear();
        } else {
            System.out.println("接收的区块链无效");
        }
    }

    @Override
    public void addTxCache(List<Transaction> txs) {
        blockChain.getTxCache().addAll(txs);
    }

    @Override
    public void removeTxCache(List<Transaction> txs){
        blockChain.getTxCache().removeAll(txs);
    }
}
