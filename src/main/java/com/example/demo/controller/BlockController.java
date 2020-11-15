package com.example.demo.controller;

import com.example.demo.entity.BlockChain;
import com.example.demo.service.BlockService;
import com.example.demo.service.ConsensusService;
import com.example.demo.serviceImpl.ConsensusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import javax.annotation.Resource;

@Controller
public class BlockController {

    @Resource
    BlockService blockService;

    @Autowired
    BlockChain blockChain;

    /**
     * 查看当前节点区块链数据
     * @return
     */
    @GetMapping("/scan")
    @ResponseBody
    public String scanBlock() {
        return JSON.toJSONString(blockChain.getBlockChain());
    }

    /**
     * 创建创世区块
     * @return
     */
    @GetMapping("/create")
    @ResponseBody
    public String createFirstBlock() {
        blockService.createGenesisBlock();
        return JSON.toJSONString(blockChain.getBlockChain());
    }

    /**
     * 工作量证明PoW
     * 挖矿生成新的区块
     */
    @GetMapping("/mine")
    @ResponseBody
    public String createNewBlock() {
        ConsensusService consServ = new ConsensusServiceImpl();
        consServ.mine();
        return JSON.toJSONString(blockChain.getBlockChain());
    }
}