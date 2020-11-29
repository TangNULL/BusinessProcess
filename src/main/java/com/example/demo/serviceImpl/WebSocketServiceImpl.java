package com.example.demo.serviceImpl;


import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Block;
import com.example.demo.entity.LocalBlockChain;
import com.example.demo.entity.NetworkMessage;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockMessageConstant;
import com.example.demo.utils.NetworkUtil;
import com.example.demo.websocket.NodeClient;
import com.example.demo.websocket.NodeServer;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class WebSocketServiceImpl implements WebSocketService, ApplicationRunner {

    BlockServiceImpl blockServiceImpl;
    LocalBlockChain blockChain;
    NodeServer nodeServer;
    NodeClient nodeClient;

    @Override
    public void broadcast(String msg) {
        List<WebSocket> socketsList = blockChain.getSockets();
        if (CollectionUtils.isEmpty(socketsList)) {
            return;
        }
        System.out.println("======全网广播消息开始：");
        for (WebSocket socket : socketsList) {
            this.write(socket, msg);
        }
        System.out.println("======全网广播消息结束");
    }

    /**
     * 获取当前区块的邻居节点
     * @return
     */
    public List<WebSocket> getSockets(){
        return blockChain.getSockets();
    }

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
    public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
        try {
            NetworkMessage networkMsg = JSON.parseObject(msg, NetworkMessage.class);
//            System.out.println("接收到IP地址为：" +webSocket.getRemoteSocketAddress().getAddress().toString()
//                    +"，端口号为："+ webSocket.getRemoteSocketAddress().getPort() + "的p2p消息："
//                    + JSON.toJSONString(networkMsg));
            switch (networkMsg.getType()) {
                //客户端请求查询最新的区块:1
                case BlockMessageConstant.QUERY_LATEST_BLOCK:
                    write(webSocket, JSON.toJSONString(new NetworkMessage(BlockMessageConstant.QUERY_LATEST_BLOCK)));//服务端调用方法返回最新区块:2
                    break;
//                //接收到服务端返回的最新区块:2
//                case BlockMessageConstant.RESPONSE_LATEST_BLOCK:
//                    handleBlockResponse(networkMsg.getData(), sockets);
//                    break;
//                //客户端请求查询整个区块链:3
//                case BlockMessageConstant.QUERY_BLOCKCHAIN:
//                    write(webSocket, JSON.toJSONString(new NetworkMessage(BlockMessageConstant.QUERY_BLOCKCHAIN)));//服务端调用方法返回最新区块:4
//                    break;
//                //接收整条区块链信息:4
//                case BlockMessageConstant.RESPONSE_BLOCKCHAIN:
//                    handleBlockChainResponse(networkMsg.getData(), sockets);
//                    break;
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
                    if (blockServiceImpl.addBlock(latestBlockReceived)) {
                        broadcast(responseLatestBlockMsg());
                    }
                    System.out.println("将新接收到的区块加入到本地的区块链");
                }
            }else if(latestBlock == null) {
                broadcast(queryBlockChainMsg());
                System.out.println("重新查询所有节点上的整条区块链");
            }
        }
    }

    @Override
    public void handleBlockChainResponse(String blockData, List<WebSocket> sockets) {
//    //反序列化得到其它节点的整条区块链信息
//        List<Block> receiveBlockchain = JSON.parseArray(blockData, Block.class);
//        if(!CollectionUtils.isEmpty(receiveBlockchain) && blockServiceImpl.isValidChain(receiveBlockchain)) {
//            //根据区块索引先对区块进行排序
//            Collections.sort(receiveBlockchain, new Comparator<Block>() {
//                public int compare(Block block1, Block block2) {
//                    return block1.getBlockId() - block2.getBlockId();
//                }
//            });
//
//            //其它节点的最新区块
//            Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
//            //当前节点的最新区块
//            Block latestBlock = blockChain.getLatestBlock();
//
//            if(latestBlock == null) {
//                //替换本地的区块链
//                blockServiceImpl.replaceChain(receiveBlockchain);
//            }else {
//                //其它节点区块链如果比当前节点的长，则处理当前节点的区块链
//                if (latestBlockReceived.getBlockId() > latestBlock.getBlockId()) {
//                    if (latestBlock.getHash().equals(latestBlockReceived.getPreHash())) {
//                        if (blockServiceImpl.addBlock(latestBlockReceived)) {
//                            broadcast(responseLatestBlockMsg());
//                        }
//                        System.out.println("将新接收到的区块加入到本地的区块链");
//                    } else {
//                        // 用长链替换本地的短链
//                        blockServiceImpl.replaceChain(receiveBlockchain);
//                    }
//                }
//            }
//        }
    }

    /**
     * 查询整条区块链
     * @return
     */
    public String queryBlockChainMsg() {
        return JSON.toJSONString(new NetworkMessage(BlockMessageConstant.QUERY_BLOCKCHAIN));
    }

    /**
     * 返回整条区块链数据
     * @return
     */
    public String responseBlockChainMsg() {
        NetworkMessage msg = new NetworkMessage();
        msg.setType(BlockMessageConstant.RESPONSE_BLOCKCHAIN);
        msg.setData(JSON.toJSONString(blockChain.getBlockChain()));
        return JSON.toJSONString(msg);
    }

    /**
     * 查询最新的区块
     * @return
     */
    public String queryLatestBlockMsg() {
        return JSON.toJSONString(new NetworkMessage(BlockMessageConstant.QUERY_LATEST_BLOCK));
    }

    /**
     * 返回最新的区块
     * @return
     */
    public String responseLatestBlockMsg() {
        NetworkMessage msg = new NetworkMessage();
        msg.setType(BlockMessageConstant.RESPONSE_LATEST_BLOCK);
        Block block = blockChain.getLatestBlock();
        msg.setData(JSON.toJSONString(block));
        return JSON.toJSONString(msg);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        blockChain = new LocalBlockChain();
        nodeServer = new NodeServer();
        nodeClient = new NodeClient();
        blockChain.setAddress(NetworkUtil.getLocalAddress());

//        blockChain.setPort(7001);
//        System.out.println("节点地址: "+blockChain.getAddress());
//        System.out.println("端口号: "+blockChain.getPort());
//        nodeServer.init(blockChain.getPort());
//        nodeClient.init(blockChain.getAddress());
//        System.out.println("*****难度系数******"+blockChain.getDifficulty());

        String[] sourceArgs = args.getSourceArgs();
        blockChain.setPort(Integer.parseInt(sourceArgs[0]));
//        for (String sourceArg : sourceArgs) {
//            System.out.println("这是传过来sourceArgs[{}]"+ sourceArg);
//        }
        // 做服务器
        if (sourceArgs.length == 1) {
            System.out.println("节点地址: "+blockChain.getAddress());
            System.out.println("端口号: "+blockChain.getPort());
            nodeServer.init(blockChain.getPort());
        }
        // 做客户端
        else{
            System.out.println("节点地址: "+blockChain.getAddress());
            System.out.println("端口号: "+blockChain.getPort());
            nodeServer.init(blockChain.getPort());
            nodeClient.init(sourceArgs[1]);
        }

    }
}
