package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.service.ConsortiumBlockchainService;
import com.example.demo.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockController {

    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain localBlockChain;

    @Autowired
    ConsortiumBlockchainService consortiumBlockchainService;

    /**
     * 查看当前节点区块链数据
     * @return
     */
    @GetMapping("/scan")
    public String scanBlock() { return JSON.toJSONString(localBlockChain.getBlockChain()); }

    /**
     * 创建创世区块
     * @return
     */
    @GetMapping("/genesis")
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

//    /**
//     * 联盟链生成
//     *
//     */
//    @GetMapping("/download")
//    @ResponseBody
//    public String downloadData() {
//        consortiumBlockchainService.downloadPhase();
//        return blockService.createNewBlock();
//    }

    /**
     * 联盟链上传数据
     *
     */
    @GetMapping("/upload")
    @ResponseBody
    public String uploadData() {
        consortiumBlockchainService.uploadPhase();
        return JSON.toJSONString(localBlockChain.getTxCache());
    }
}