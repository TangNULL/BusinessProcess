package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.LocalConsortiumBlockchain;
import com.example.demo.entity.LocalPublicBlockchain;
import com.example.demo.entity.NetworkMsg;
import com.example.demo.entity.User;
import com.example.demo.service.*;
import com.example.demo.utils.NetworkUtil;
import com.example.demo.utils.BlockchainUtil;
import com.example.demo.websocket.NodeClient;
import com.example.demo.websocket.NodeServer;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebSocketServiceImpl implements WebSocketService, ApplicationRunner {
    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain localPublicBlockchain;

    @Autowired
    LocalConsortiumBlockchain localConsortiumBlockchain;

    @Autowired
    NodeServer nodeServer;

    @Autowired
    NodeClient nodeClient;

    @Autowired
    PublicBlockchainService publicBlockchainService;

    @Autowired
    ConsortiumBlockchainService consortiumBlockchainService;

    @Override
    public void write(WebSocket ws, String msg) {
        System.out.println("发送给IP地址为：" +ws.getRemoteSocketAddress().getAddress().toString()
                + "，端口号为："+ws.getRemoteSocketAddress().getPort() + " 的p2p消息:" + msg);
        ws.send(msg);
    }

    @Override
    public void broadcast(String msg, List<WebSocket> sockets) {
        if (CollectionUtils.isEmpty(sockets)) {
            return;
        }
        System.out.println("====== 全网广播消息开始：======");
        for (WebSocket socket : sockets) {
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
                case BlockchainUtil.QUERY_LATEST_BLOCK:
                    write(webSocket, publicBlockchainService.responseLatestBlockMsg());//服务端调用方法返回最新区块:2
                    break;
                //接收到服务端返回的最新区块:2
                case BlockchainUtil.RESPONSE_LATEST_BLOCK:
                    publicBlockchainService.handleBlockResponse(networkMsg.getData(), sockets);
                    break;
                //客户端请求查询整个区块链:3
                case BlockchainUtil.QUERY_BLOCKCHAIN:
                    write(webSocket, publicBlockchainService.responseBlockChainMsg());//服务端调用方法返回最新区块:4
                    break;
                //接收整条区块链信息:4
                case BlockchainUtil.RESPONSE_BLOCKCHAIN:
                    publicBlockchainService.handleBlockChainResponse(networkMsg.getData(), sockets);
                    break;

                case BlockchainUtil.APPLY_DENY:
                    write(webSocket, consortiumBlockchainService.applyDeny());
                    break;
                //向其他参与者发起协作申请:1
                case BlockchainUtil.APPLY_FOR_COOPERATION:
                    write(webSocket, consortiumBlockchainService.applyForCooperation());
                    break;
                //协作者同意加入:2
                case BlockchainUtil.CONFIRM_FOR_COOPERATION:
                    write(webSocket, consortiumBlockchainService.confirmCooperation());
                    break;
            }
        } catch (Exception e) {
            System.out.println("处理IP地址为：" +webSocket.getRemoteSocketAddress().getAddress().toString()
                    +"，端口号为："+ webSocket.getRemoteSocketAddress().getPort() + "的p2p消息错误:"
                    + e.getMessage());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //添加区块链种子节点
        List<String> seedNodes =  new ArrayList<>();
        seedNodes.add(BlockchainUtil.SEED_NODE1);
        seedNodes.add(BlockchainUtil.SEED_NODE2);
        seedNodes.add(BlockchainUtil.SEED_NODE3);

        //设置区块链ip和端口
        localPublicBlockchain.setAddress(NetworkUtil.getLocalAddress());
        String[] sourceArgs = args.getSourceArgs();
        localPublicBlockchain.setPort(Integer.parseInt(sourceArgs[0]));

        //设置种子用户
        List<User> userList = new ArrayList<User>();
        userList.add(new User("u1", "123456", "u1", "des", "core", "ass"));
        userList.add(new User("u2", "123456", "u1", "des", "core", "ass"));
        localPublicBlockchain.setUsers(userList);

        System.out.println("节点地址: "+localPublicBlockchain.getAddress());
        System.out.println("端口号: "+localPublicBlockchain.getPort());
        nodeServer.init(localPublicBlockchain.getPort());
        for (String seedNode : seedNodes) {
            if (!seedNode.equals("ws://"+localPublicBlockchain.getAddress()+":"+localPublicBlockchain.getPort())) {
                nodeClient.init(seedNode);
                if (!localPublicBlockchain.getBlockChain().isEmpty()) {
                    break;
                }
            }
        }
        if (localPublicBlockchain.getBlockChain().isEmpty()) {
            blockService.createGenesisBlock();
            System.out.println("创建创世区块: "+localPublicBlockchain.getBlockChain().get(0).getHash());
        }
    }

}
