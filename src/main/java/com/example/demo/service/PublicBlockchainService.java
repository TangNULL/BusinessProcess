package com.example.demo.service;

import org.java_websocket.WebSocket;
import java.util.List;

/**
 * 区块链网络层消息传递
 */
public interface PublicBlockchainService {
    //公有链传递的消息
    String queryLatestBlockMsg();  //查询最新的区块
    String responseLatestBlockMsg();  //返回最新的区块
    String queryBlockChainMsg();  //查询整条区块链
    String responseBlockChainMsg();  //返回整条区块链

    void handleBlockResponse(String blockData, List<WebSocket> sockets);  //接受挖矿节点挖出的最新区块，如果本地区块链高度过低，更新整个区块链
    void handleBlockChainResponse(String blockData, List<WebSocket> sockets);  //更新本地节点整个区块链
}
