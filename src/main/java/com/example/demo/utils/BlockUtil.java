package com.example.demo.utils;

/**
 * 定义了节点间区块同步的消息类型，创世区块信息
 */
public class BlockUtil {
    //查询最新的区块
    public final static int QUERY_LATEST_BLOCK = 1;

    //返回最新的区块
    public final static int RESPONSE_LATEST_BLOCK = 2;

    //查询整个区块链
    public final static int QUERY_BLOCKCHAIN = 3;

    //返回整个区块链
    public final static int RESPONSE_BLOCKCHAIN = 4;

    //TODO:
s

    //区块链种子节点
    public final static String SEED_NODE1 = "ws://172.19.187.200:7005";
    public final static String SEED_NODE2 = "ws://172.19.187.200:7006";
    public final static String SEED_NODE3 = "ws://172.19.187.200:7007";

}
