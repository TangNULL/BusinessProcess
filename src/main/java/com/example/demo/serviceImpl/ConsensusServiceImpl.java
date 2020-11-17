package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Block;
import com.example.demo.entity.BlockChain;
import com.example.demo.entity.User;
import com.example.demo.service.ConsensusService;
import com.example.demo.utils.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsensusServiceImpl implements ConsensusService {

    @Override
    public Block PoWMine(BlockChain blockChain) {
        Block newBlock = new Block();
        Block preBlock = blockChain.getLatestBlock();
        // 新区块属性
        newBlock.setBlockId(blockChain.getBlockChain().size() + 1);
        newBlock.setTimestamp(System.currentTimeMillis());
        newBlock.setPreHash(preBlock.getHash());

        List<User> userList2 = new ArrayList<User>();
        userList2.add(new User("u3", "123456", "u1", "des", "core", "ass"));
        userList2.add(new User("u4", "123456", "u1", "des", "core", "ass"));
        newBlock.setUsers(userList2);

        //挖矿难度
        newBlock.setTarget("0000");

        //工作量证明，计算正确hash值的次数
        int nonce = 0;
        long start = System.currentTimeMillis();
        System.out.println("开始挖矿");
        while (true) {
            // 计算新区块hash值
            newBlock.setNonce(nonce);
            newBlock.setHash(CryptoUtil.calcBlockHash(newBlock));
            // 校验hash值
            if (isValidHash(newBlock.getHash(), newBlock.getTarget())) {
                System.out.println("挖矿完成，正确的hash值：" + newBlock.getHash());
                System.out.println("挖矿耗费时间：" + (System.currentTimeMillis() - start) + "ms");
                break;
            }
            System.out.println("第"+(nonce+1)+"次尝试计算的hash值：" + newBlock.getHash());
            nonce++;

            //如果更新了区块链，则放弃挖矿
        }

        return newBlock;
    }

    /**
     * 验证是否挖到新的区块,暂时这样写，后续修改
     *
     * @param hash
     * @param target
     * @return
     */

    @Override
    public boolean isValidHash(String hash, String target) {
        return hash.startsWith("0000");
    }

}
