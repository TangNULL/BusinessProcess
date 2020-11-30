package com.example.demo.serviceImpl;


import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Block;
import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.entity.NetworkMsg;
import com.example.demo.service.BlockService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockUtil;
import com.example.demo.utils.NetworkUtil;
import com.example.demo.websocket.NodeClient;
import com.example.demo.websocket.NodeServer;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class WebSocketServiceImpl implements WebSocketService, ApplicationRunner {

    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain blockChain;

    @Autowired
    NodeServer nodeServer;

    @Autowired
    NodeClient nodeClient;

    /**
     * 向其它节点发送消息
     * @param ws
     * @param msg
     */
    public void write(WebSocket ws, String msg) {
        System.out.println("发送给IP地址为：" +ws.getRemoteSocketAddress().getAddress().toString()
                + "，端口号为："+ws.getRemoteSocketAddress().getPort() + " 的p2p消息:" + msg);
        ws.send(msg);
    }

    @Override
    public void broadcast(String msg) {
        List<WebSocket> socketsList = blockChain.getSockets();
        if (CollectionUtils.isEmpty(socketsList)) {
            return;
        }
        System.out.println("====== 全网广播消息开始：======");
        for (WebSocket socket : socketsList) {
            this.write(socket, msg);
        }
        System.out.println("====== 全网广播消息结束 ======");
    }

    @Override
    public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
        try {
            NetworkMsg networkMsg = JSON.parseObject(msg, NetworkMsg.class);
            System.out.println("接收到IP地址为：" +webSocket.getRemoteSocketAddress().getAddress().toString()
                    +"，端口号为："+ webSocket.getRemoteSocketAddress().getPort() + " 的p2p消息："
                    + JSON.toJSONString(networkMsg));
            switch (networkMsg.getType()) {
                //客户端请求查询最新的区块:1
                case BlockUtil.QUERY_LATEST_BLOCK:
                    write(webSocket, responseLatestBlockMsg());//服务端调用方法返回最新区块:2
                    break;
                //接收到服务端返回的最新区块:2
                case BlockUtil.RESPONSE_LATEST_BLOCK:
                    handleBlockResponse(networkMsg.getData(), sockets);
                    break;
                //客户端请求查询整个区块链:3
                case BlockUtil.QUERY_BLOCKCHAIN:
                    write(webSocket, responseBlockChainMsg());//服务端调用方法返回最新区块:4
                    break;
                //接收整条区块链信息:4
                case BlockUtil.RESPONSE_BLOCKCHAIN:
                    handleBlockChainResponse(networkMsg.getData(), sockets);
                    break;
            }
        } catch (Exception e) {
            System.out.println("处理IP地址为：" +webSocket.getRemoteSocketAddress().getAddress().toString()
                    +"，端口号为："+ webSocket.getRemoteSocketAddress().getPort() + "的p2p消息错误:"
                    + e.getMessage());
        }
    }

    @Override
    public void handleBlockResponse(String blockData, List<WebSocket> sockets) {
        //反序列化得到其它节点的最新区块信息
        Block latestBlockReceived = JSON.parseObject(blockData, Block.class);
        //当前节点的最新区块
        Block latestBlock = blockChain.getLatestBlock();

        if (latestBlockReceived != null) {
            if(latestBlock != null) {
                //如果接收到的区块高度比本地区块高度大的多
                if(latestBlockReceived.getBlockId() > latestBlock.getBlockId() + 1) {
                    broadcast(queryBlockChainMsg());
                    System.out.println("重新查询所有节点上的整条区块链");
                }else if (latestBlockReceived.getBlockId() > latestBlock.getBlockId() &&
                        latestBlock.getHash().equals(latestBlockReceived.getPreHash())) {
                    if (blockService.addBlock(latestBlockReceived)) {
                        broadcast(responseLatestBlockMsg());
                    }
                    System.out.println("将新接收到的区块加入到本地的区块链");
                }
            }else {
                broadcast(queryBlockChainMsg());
                System.out.println("重新查询所有节点上的整条区块链");
            }
        }
    }

    @Override
    public void handleBlockChainResponse(String blockData, List<WebSocket> sockets) {
    //反序列化得到其它节点的整条区块链信息
        List<Block> receiveBlockchain = JSON.parseArray(blockData, Block.class);
        if(!CollectionUtils.isEmpty(receiveBlockchain) && blockService.isValidChain(receiveBlockchain)) {
            //根据区块索引先对区块进行排序
            Collections.sort(receiveBlockchain, new Comparator<Block>() {
                public int compare(Block block1, Block block2) {
                    return block1.getBlockId() - block2.getBlockId();
                }
            });

            //其它节点的最新区块
            Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size()-1);
            //当前节点的最新区块
            Block latestBlock = blockChain.getLatestBlock();

            if(latestBlock == null) {
                //替换本地的区块链
                blockService.replaceChain(receiveBlockchain);
            }else {
                //其它节点区块链如果比当前节点的长，则处理当前节点的区块链
                if (latestBlockReceived.getBlockId() > latestBlock.getBlockId()) {
                    if (latestBlock.getHash().equals(latestBlockReceived.getPreHash())) {
                        if (blockService.addBlock(latestBlockReceived)) {
                            broadcast(responseLatestBlockMsg());
                        }
                        System.out.println("将新接收到的区块加入到本地的区块链");
                    } else {
                        // 用长链替换本地的短链
                        blockService.replaceChain(receiveBlockchain);
                    }
                }
            }
        }
    }


    /**
     * 查询最新的区块
     * @return
     */
    public String queryLatestBlockMsg() {
        return JSON.toJSONString(new NetworkMsg(BlockUtil.QUERY_LATEST_BLOCK));
    }

    /**
     * 返回最新的区块
     * @return
     */
    public String responseLatestBlockMsg() {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockUtil.RESPONSE_LATEST_BLOCK);
        msg.setData(JSON.toJSONString(blockChain.getLatestBlock()));
        return JSON.toJSONString(msg);
    }

    /**
     * 查询整条区块链
     * @return
     */
    public String queryBlockChainMsg() {
        return JSON.toJSONString(new NetworkMsg(BlockUtil.QUERY_BLOCKCHAIN));
    }

    /**
     * 返回整条区块链数据
     * @return
     */
    public String responseBlockChainMsg() {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockUtil.RESPONSE_BLOCKCHAIN);
        msg.setData(JSON.toJSONString(blockChain.getBlockChain()));
        return JSON.toJSONString(msg);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //添加区块链种子节点
        List<String> seedNodes =  new ArrayList<>();
        seedNodes.add(BlockUtil.SEED_NODE1);
        seedNodes.add(BlockUtil.SEED_NODE2);
        seedNodes.add(BlockUtil.SEED_NODE3);

        blockChain.setAddress(NetworkUtil.getLocalAddress());

        String[] sourceArgs = args.getSourceArgs();
        blockChain.setPort(Integer.parseInt(sourceArgs[0]));

        System.out.println("节点地址: "+blockChain.getAddress());
        System.out.println("端口号: "+blockChain.getPort());
        nodeServer.init(blockChain.getPort());
        for (String seedNode : seedNodes) {
            if (!seedNode.equals("ws://"+blockChain.getAddress()+":"+blockChain.getPort())) {
                nodeClient.init(seedNode);
                if (!blockChain.getBlockChain().isEmpty()) {
                    break;
                }
            }
        }
    }
}
