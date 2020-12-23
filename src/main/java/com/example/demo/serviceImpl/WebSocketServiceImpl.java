package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.LocalCooperation;
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
        System.out.println("====== 全网广播消息开始 ======");
        for (WebSocket socket : sockets) {
            this.write(socket, msg);
        }
        System.out.println("====== 全网广播消息结束 ======");
    }

    @Override
    public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
        try {
            NetworkMsg networkMsg = JSON.parseObject(msg, NetworkMsg.class);
            System.out.println("接收到IP地址为：" + webSocket.getRemoteSocketAddress().getAddress().toString()
                    +"，端口号为："+ webSocket.getRemoteSocketAddress().getPort() + " 的p2p消息："
                    + JSON.toJSONString(networkMsg));
            switch (networkMsg.getType()) {

                case BlockchainUtil.QUERY_LATEST_BLOCK:
                    //客户端请求查询最新的区块:1, 服务端调用方法返回最新区块:2
                    write(webSocket, publicBlockchainService.responseLatestBlockMsg());
                    break;

                case BlockchainUtil.RESPONSE_LATEST_BLOCK:
                    //接收到服务端返回的最新区块:2
                    publicBlockchainService.handleBlockResponse(networkMsg.getData(), sockets);
                    break;

                case BlockchainUtil.QUERY_BLOCKCHAIN:
                    //客户端请求查询整个区块链:3, 服务端调用方法返回最新区块:4
                    write(webSocket, publicBlockchainService.responseBlockChainMsg());
                    break;

                case BlockchainUtil.RESPONSE_BLOCKCHAIN:
                    //接收整条区块链信息:4
                    publicBlockchainService.handleBlockChainResponse(networkMsg.getData(), sockets);
                    break;

                case BlockchainUtil.APPLY_FOR_COOPERATION:
                    //处理其他协作者的协作申请:5
                    System.out.println("接收到用户 " + webSocket.getRemoteSocketAddress().getAddress().toString() +
                            " 发往 " +  webSocket.getLocalSocketAddress().getAddress().toString() +" 的申请合作请求");
                    consortiumBlockchainService.handleApplyForCooperation(networkMsg.getData());
                    break;

                case BlockchainUtil.CONFIRM_FOR_COOPERATION:
                case BlockchainUtil.APPLY_DENY:
                    //处理其他协作者同意协作回复:6 和 其他协作者拒绝协作回复:0
                    System.out.println("收到 " + webSocket.getRemoteSocketAddress().getAddress().toString() + " 的确认回复");
                    consortiumBlockchainService.handleConfirmCooperation(networkMsg);
                    break;
                case BlockchainUtil.APPLY_FOR_UPLOAD:
                    //处理其他协作者的申请上传请求：7
                    System.out.println("接收到用户 " + webSocket.getRemoteSocketAddress().getAddress().toString() +
                            " 发往 " +  webSocket.getLocalSocketAddress().getAddress().toString() +" 的申请上传请求");
                    consortiumBlockchainService.handleApplyForUpLoad(networkMsg.getData());
                    break;
                case BlockchainUtil.CONFIRM_FOR_UPLOAD:
                    //处理其他协作者的同意上传请求：8
                    System.out.println("接收到用户 " + webSocket.getRemoteSocketAddress().getAddress().toString() +
                            " 发往 " +  webSocket.getLocalSocketAddress().getAddress().toString() +" 的同意上传请求");
                    consortiumBlockchainService.handleConfirmForUpLoad(networkMsg.getData());
                    break;
            }
        } catch (Exception e) {
            System.out.println("处理IP地址为：" +webSocket.getRemoteSocketAddress().getAddress().toString()
                    +"，端口号为："+ webSocket.getRemoteSocketAddress().getPort() + "的p2p消息错误:"
                    + e.getMessage());
        }
    }

    @Override
    public void run(ApplicationArguments args) {

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
        List<User> userList = new ArrayList<>();
        userList.add(new User("u1", "123456", "u1", "des", "core", "ass"));
        userList.add(new User("u2", "123456", "u1", "des", "core", "ass"));
        userList.add(new User("u3", "123456", "u1", "des", "core", "ass"));
        localPublicBlockchain.setUsers(userList);

        System.out.println("节点地址: "+localPublicBlockchain.getAddress());
        System.out.println("端口号: "+localPublicBlockchain.getPort());
        nodeServer.init(localPublicBlockchain.getPort());
        for (String seedNode : seedNodes) {
            if (!seedNode.equals("ws://"+ localPublicBlockchain.getAddress()+":"+ localPublicBlockchain.getPort())) {
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
