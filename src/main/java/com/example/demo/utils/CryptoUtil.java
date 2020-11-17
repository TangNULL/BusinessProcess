package com.example.demo.utils;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Block;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 哈希函数和加密函数
 */
public class CryptoUtil {
    /**
     * sha256 哈希函数
     * @param str
     * @return encodeStr
     */
    public static String SHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            System.out.println("getSHA256 is error" + e.getMessage());
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String temp;
        for (byte tempByte : bytes) {
            temp = Integer.toHexString(tempByte & 0xFF);
            if (temp.length() == 1) {
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }

    /**
     * 计算当前区块的哈希值
     *
     * @param curBlock
     * @return 当前区块的哈希值
     */
    public static String calcBlockHash(Block curBlock) {
        String preHash = curBlock.getPreHash();
        String strUser = JSON.toJSONString(curBlock.getUsers());
        String strTx = JSON.toJSONString(curBlock.getTxs());
        String nonce = String.valueOf(curBlock.getNonce());
        return CryptoUtil.SHA256(preHash + strUser + strTx + nonce);
    }
}
