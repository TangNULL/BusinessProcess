package com.example.demo.serviceImpl;

import com.example.demo.entity.LocalConsortiumBlockchain;
import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.entity.Transaction;
import com.example.demo.service.BPRecordService;
import com.example.demo.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BPRecordServiceImpl implements BPRecordService {
    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain blockChain;

    @Autowired
    LocalConsortiumBlockchain consortiumBlockchain;

    @Override
    public boolean downloadPhase() {
        //首先添加自己的事件
        consortiumBlockchain.setUsers();
        consortiumBlockchain.setTxs();

        //TODO: 通知其他用户参与协作


        return true;
    }

    @Override
    public boolean generatePhase() {
        return false;
    }

    @Override
    public boolean uploadPhase() {
        //TODO: 将联盟链数据传到公有链的上
        blockService.addTxCache(consortiumBlockchain.getTxs());

        //销毁联盟链
        consortiumBlockchain.getUsers().clear();
        consortiumBlockchain.getTxs().clear();
        consortiumBlockchain.getPkHash().clear();
        return true;
    }
}
