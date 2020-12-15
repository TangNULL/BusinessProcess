package com.example.demo.serviceImpl;


import com.alibaba.fastjson.JSON;
import com.example.demo.entity.PublicBlock;
import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.entity.NetworkMsg;
import com.example.demo.service.BlockService;
import com.example.demo.service.PublicBlockchainService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockchainUtil;
import com.example.demo.websocket.NodeClient;
import com.example.demo.websocket.NodeServer;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.Comparator;
import java.util.List;

@Service
public class PublicBlockchainServiceImpl implements PublicBlockchainService {

    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain localPublicBlockchain;

    @Autowired
    NodeServer nodeServer;

    @Autowired
    NodeClient nodeClient;

    @Autowired
    WebSocketService webSocketService;

    @Override
    public String queryLatestBlockMsg() {
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.QUERY_LATEST_BLOCK));
    }

    @Override
    public String responseLatestBlockMsg() {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.RESPONSE_LATEST_BLOCK);
        msg.setData(JSON.toJSONString(localPublicBlockchain.getLatestBlock()));
        return JSON.toJSONString(msg);
    }

    @Override
    public String queryBlockChainMsg() {
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.QUERY_BLOCKCHAIN));
    }

    @Override
    public String responseBlockChainMsg() {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.RESPONSE_BLOCKCHAIN);
        msg.setData(JSON.toJSONString(localPublicBlockchain.getBlockChain()));
        return JSON.toJSONString(msg);
    }

    @Override
    public void handleBlockResponse(String blockData, List<WebSocket> sockets) {
        //反序列化得到其它节点的最新区块信息
        PublicBlock receivedLatestBlock = JSON.parseObject(blockData, PublicBlock.class);
        //当前节点的最新区块
        PublicBlock localLatestBlock = localPublicBlockchain.getLatestBlock();

        if (receivedLatestBlock != null) {
            if(localLatestBlock != null) {
                if (receivedLatestBlock.getBlockId() == localLatestBlock.getBlockId() + 1 &&
                        localLatestBlock.getHash().equals(receivedLatestBlock.getPreHash())) {
                    if (blockService.addBlock(receivedLatestBlock)) {
                        webSocketService.broadcast(responseLatestBlockMsg(), localPublicBlockchain.getSockets());
                    }
                    System.out.println("将新接收到的区块加入到本地的区块链");
                }
            }else {
                webSocketService.broadcast(queryBlockChainMsg(), localPublicBlockchain.getSockets());
                System.out.println("重新查询所有节点上的整条区块链");
            }
        }
    }

    @Override
    public void handleBlockChainResponse(String blockData, List<WebSocket> sockets) {
        //反序列化得到其它节点的整条区块链信息
        List<PublicBlock> receivedBlockchain = JSON.parseArray(blockData, PublicBlock.class);
        if(!CollectionUtils.isEmpty(receivedBlockchain) && blockService.isValidChain(receivedBlockchain)) {
            //根据区块索引先对区块进行排序
            receivedBlockchain.sort(new Comparator<PublicBlock>() {
                public int compare(PublicBlock block1, PublicBlock block2) {
                    return block1.getBlockId() - block2.getBlockId();
                }
            });

            //其它节点的最新区块
            PublicBlock latestBlockReceived = receivedBlockchain.get(receivedBlockchain.size()-1);
            //当前节点的最新区块
            PublicBlock latestBlock = localPublicBlockchain.getLatestBlock();

            if(latestBlock == null) {
                //替换本地的区块链
                blockService.replaceChain(receivedBlockchain);
            }else {
                //如果只领先本地一个区块，则更新最新区块
                if (latestBlockReceived.getBlockId() > latestBlock.getBlockId()) {
                    if (latestBlock.getHash().equals(latestBlockReceived.getPreHash())) {
                        if (blockService.addBlock(latestBlockReceived)) {
                            webSocketService.broadcast(responseLatestBlockMsg(), localPublicBlockchain.getSockets());
                        }
                        System.out.println("将新接收到的区块加入到本地的区块链");
                    } else {
                        // 用长链替换本地的短链
                        blockService.replaceChain(receivedBlockchain);
                    }
                }
            }
        }
    }
}
