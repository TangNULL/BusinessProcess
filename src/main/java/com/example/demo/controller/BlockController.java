package com.example.demo.controller;

import com.example.demo.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockController {

    @Autowired
    BlockService blockService;

    /**
     * 查看当前节点区块链数据
     * @return
     */
    @GetMapping("/scan")
    public String scanBlock() {
        return blockService.getBlockChain();
    }

    /**
     * 创建创世区块
     * @return
     */
    @GetMapping("/create")
    public String createFirstBlock() {
        return blockService.createGenesisBlock();
    }

    /**
     * 工作量证明PoW
     * 挖矿生成新的区块
     */
    @GetMapping("/mine")
    @ResponseBody
    public String createNewBlock() {
        return blockService.createNewBlock();
    }
}