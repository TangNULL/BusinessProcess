package com.example.demo.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {
    //区块链通信占用的端口
    public final static int BLOCK_CHAIN_PORT = 12345;

    /**
     * 获取本地ip
     */
    public static String getLocalAddress() {
        try {
            InetAddress ip4 = InetAddress.getLocalHost();
            return ip4.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 将用户id转化为对应的用户ip地址
     * @param userId 用户id
     * @return 对应用户ip
     */
    public static String getAddressById(Integer userId) {
        //在数据库查找用户

        //这里做的是个测试代码
        switch (userId) {
            case 1:
                return BlockchainUtil.SEED_NODE1;
            case 2:
                return BlockchainUtil.SEED_NODE2;
            case 3:
                return BlockchainUtil.SEED_NODE3;
        }
        return null;
    }

    /**
     * 将传来的ip地址转化为用户id
     * @param ipAddress
     * @return
     */
    public static Integer getIdByAddress(String ipAddress) {
        return 0;
    }
}
