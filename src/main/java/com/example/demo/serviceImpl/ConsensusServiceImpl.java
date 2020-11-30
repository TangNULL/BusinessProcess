package com.example.demo.serviceImpl;

import com.example.demo.entity.Block;
import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.service.ConsensusService;
import com.example.demo.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsensusServiceImpl implements ConsensusService {

    @Autowired
    LocalPublicBlockchain blockChain;

    @Override
    public Block PoWMine() {
        Block newBlock = new Block();
        Block preBlock = blockChain.getLatestBlock();
        // 新区块属性
        newBlock.setBlockId(preBlock.getBlockId() + 1);
        newBlock.setTimestamp(System.currentTimeMillis());
        newBlock.setPreHash(preBlock.getHash());

        // 需要从交易池提取获得的数据
        for (Transaction tx: blockChain.getTxCache()) {

        }

        List<User> userList2 = new ArrayList<User>();
        userList2.add(new User("u3", "123456", "u1", "des", "core", "ass"));
        userList2.add(new User("u4", "123456", "u1", "des", "core", "ass"));
        newBlock.setUsers(userList2);

        //挖矿难度
        newBlock.setDifficulty(changeDifficulty(preBlock.getDifficulty()));

        //工作量证明，计算正确hash值的次数
        int nonce = 0;
        long start = System.currentTimeMillis();
        System.out.println("开始挖矿");
        while (true) {
            // 计算新区块hash值
            newBlock.setNonce(nonce);
            newBlock.setHash(CryptoUtil.calcBlockHash(newBlock));
            // 校验hash值
            if (isValidHash(newBlock.getHash(), newBlock.getDifficulty())) {
                System.out.println("挖矿完成，正确的hash值：" + newBlock.getHash());
                System.out.println("挖矿耗费时间：" + (System.currentTimeMillis() - start) + "ms");
                break;
            }
            System.out.println("第"+(nonce+1)+"次尝试计算的hash值：" + newBlock.getHash());
            nonce++;

            //如果本地区块链接收到了新的区块，更新交易池，开始新的挖矿
        }
        return newBlock;
    }

    @Override
    public Block PoSMine() {
        Block newBlock = new Block();
        return newBlock;
    }

    @Override
    public boolean isValidHash(String hash, int difficulty) {
        String prefix = repeat("0", difficulty);
        return hash.startsWith(prefix);
    }

    /**
     * 同一个字符串重复多次
     * @param str
     * @param repeat
     * @return
     */
    private static String repeat(String str, int repeat) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            buf.append(str);
        }
        return buf.toString();
    }

    /**
     * 改变区块链生成难度，暂时是固定的不改变
     * @param curDifficulty
     * @return
     */
    private int changeDifficulty(int curDifficulty) {
        return curDifficulty;
    }
}
