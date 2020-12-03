package com.example.demo.utils;

/**
 * 定义了节点间公有链区块同步的消息类型，种子节点地址
 */
public class BlockchainUtil {

    //查询最新的区块
    public final static int QUERY_LATEST_BLOCK = 1;

    //返回最新的区块
    public final static int RESPONSE_LATEST_BLOCK = 2;

    //查询整个区块链
    public final static int QUERY_BLOCKCHAIN = 3;

    //返回整个区块链
    public final static int RESPONSE_BLOCKCHAIN = 4;

    //申请不通过
    public final static int APPLY_DENY = 0;

    //邀请协作者
    public final static int APPLY_FOR_COOPERATION = 5;

    //接受邀请，加入联盟链
    public final static int CONFIRM_FOR_COOPERATION = 6;

    //区块链种子节点
    public final static String SEED_NODE1 = "ws://172.19.187.200:7005";
    public final static String SEED_NODE2 = "ws://172.19.187.200:7006";
    public final static String SEED_NODE3 = "ws://172.19.187.200:7007";

}
