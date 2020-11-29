package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Block;
import com.example.demo.entity.LocalBlockChain;
import com.example.demo.entity.User;
import com.example.demo.service.BlockService;
import com.example.demo.utils.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlockServiceImpl implements BlockService {

    LocalBlockChain blockChain = new LocalBlockChain();

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
    // 验证是否为最新区块
    public boolean isValidBlock(Block lastBlock, Block curBlock) {
        if(lastBlock.getHash().equals(curBlock.getPreHash())){
            return true;
        }
        return false;
    }

    @Override
    public String getBlockChain() {
        return JSON.toJSONString(blockChain.getBlockChain());
    }

//    @Override
//    public boolean isValidChain(List<Block> otherChain) {
//        Block block = null;
//        Block lastBlock = otherChain.get(0);
//        int currentIndex = 1;
//        while (currentIndex < otherChain.size()) {
//            block = otherChain.get(currentIndex);
//            if (!isValidBlock(block, lastBlock)) {
//                return false;
//            }
//            lastBlock = block;
//            currentIndex++;
//        }
//        return true;
//    }
//
//    @Override
//    public void replaceChain(List<Block> otherChain) {
//        List<Block> localBlockChain = blockChain.getBlockChain();
////        List<Transaction> localpackedTransactions = blockChain.getPackedTransactions();
//        if (isValidChain(otherChain) && otherChain.size() > localBlockChain.size()) {
//            localBlockChain = otherChain;
//            //替换已打包保存的业务数据集合
////            localpackedTransactions.clear();
////            localBlockChain.forEach(block -> {
////                localpackedTransactions.addAll(block.getTransactions());
////            });
//            blockChain.setBlockChain(otherChain);
////            blockChain.setPackedTransactions(localpackedTransactions);
//            System.out.println("替换后的本节点区块链："+JSON.toJSONString(blockChain.getBlockChain()));
//        } else {
//            System.out.println("接收的区块链无效");
//        }
//    }

    @Override
    public void removeTxCache(Block newBlock){
        blockChain.getTxCache().removeAll(newBlock.getTxs());
    }
}
