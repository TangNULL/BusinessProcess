package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.*;
import com.example.demo.service.BlockService;
import com.example.demo.service.ConsensusService;
import com.example.demo.service.PublicBlockchainService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockchainUtil;
import com.example.demo.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlockServiceImpl implements BlockService {

    @Autowired
    ConsensusService consensusService;

    @Autowired
    PublicBlockchainService publicBlockchainService;

    @Autowired
    LocalPublicBlockchain localPublicBlockchain;

    @Autowired
    WebSocketService webSocketService;


    @Override
    public String createGenesisBlock() {
        if (localPublicBlockchain.getBlockChain().size() == 0) {
            PublicBlock genesisBlock = new PublicBlock();
            // 创世区块属性
            genesisBlock.setBlockId(1);
            genesisBlock.setTimestamp(System.currentTimeMillis());
            genesisBlock.setNonce(1);
            genesisBlock.setPreHash("");
            genesisBlock.setDifficulty(4);

            //设置用户状态
            StringBuilder localUserState = new StringBuilder();
            for (User user : localPublicBlockchain.getUsers()) {
                localUserState.append(user.getUserid());
            }
            genesisBlock.setUsersState(CryptoUtil.SHA256(localUserState.toString()));
            genesisBlock.setHash(CryptoUtil.calcBlockHash(genesisBlock));
            //添加到区块链中
            localPublicBlockchain.getBlockChain().add(genesisBlock);
            //通知其他节点
            createBlockBroadcast(genesisBlock);

            return JSON.toJSONString(genesisBlock);
        }
        return JSON.toJSONString(localPublicBlockchain.getBlockChain().get(0));
    }

    @Override
    public String createNewBlock() {
        PublicBlock newBlock = consensusService.PoWMine();

        if (addBlock(newBlock)) {
            //通知其他节点
            createBlockBroadcast(newBlock);
            return JSON.toJSONString(newBlock);
        }
        else {
            return "Error!";
        }
    }

    private void createBlockBroadcast(PublicBlock block) {
        //创建成功后，全网广播出去
        NetworkMsg networkMsg = new NetworkMsg();
        networkMsg.setType(BlockchainUtil.RESPONSE_LATEST_BLOCK);
        networkMsg.setData(JSON.toJSONString(block));
        webSocketService.broadcast(JSON.toJSONString(networkMsg), localPublicBlockchain.getSockets());
    }

    @Override
    public boolean addBlock(PublicBlock newBlock) {
        //先对新区块的合法性进行校验
        if (isValidBlock(localPublicBlockchain.getLatestBlock(), newBlock)) {
            localPublicBlockchain.getBlockChain().add(newBlock);
            localPublicBlockchain.removeTxCache(newBlock.getTxs());
            return true;
        }
        return false;
    }

    //验证是否为最新区块
    private boolean isValidBlock(PublicBlock lastBlock, PublicBlock curBlock) {
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
    public boolean isValidChain(List<PublicBlock> otherChain) {
        for(int i = 0; i < otherChain.size()-1; i++) {
            if (!isValidBlock(otherChain.get(i), otherChain.get(i+1))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void replaceChain(List<PublicBlock> otherChain) {
        if (otherChain.size() > localPublicBlockchain.getBlockChain().size() && isValidChain(otherChain)) {
            localPublicBlockchain.setBlockChain(otherChain);
            localPublicBlockchain.getTxCache().clear();
        } else {
            System.out.println("接收的区块链无效");
        }
    }
}
