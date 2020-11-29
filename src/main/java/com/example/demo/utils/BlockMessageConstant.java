package com.example.demo.utils;

/**
 * 定义了节点间区块同步的消息类型
 */
public class BlockMessageConstant {
    // 查询最新的区块
    public final static int QUERY_LATEST_BLOCK = 1;

    // 返回最新的区块
    public final static int RESPONSE_LATEST_BLOCK = 2;

    // 查询整个区块链
    public final static int QUERY_BLOCKCHAIN = 3;

    // 返回整个区块链
    public final static int RESPONSE_BLOCKCHAIN = 4;
}
