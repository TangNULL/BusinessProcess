package com.example.demo.utils;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.PublicBlock;
import com.example.demo.entity.Transaction;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 哈希函数和加密函数
 */
public class CryptoUtil {
    /**
     * sha256 哈希函数
     * @param str 需要加密的字符串
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
     * md5 哈希函数
     * @param str
     * @return
     */
    public static String MD5(String str) {
        String resultStr = DigestUtils.md5DigestAsHex(str.getBytes());
        return resultStr.substring(4, resultStr.length());
    }

    /**
     * 对交易内容加密
     * @param tx 需要加密的交易
     * @return 加密后的交易
     */
    public static String encryptTx(Transaction tx) {
        return JSON.toJSONString(tx);
    }

    /**
     * 计算当前区块的哈希值
     *
     * @param curBlock 需要计算哈希值的区块
     * @return 当前区块的哈希值
     */
    public static String calcBlockHash(PublicBlock curBlock) {
        String preHash = curBlock.getPreHash();
        String strUser = JSON.toJSONString(curBlock.getUsersState());
        String strTx = JSON.toJSONString(curBlock.getTxs());
        String nonce = String.valueOf(curBlock.getNonce());
        return CryptoUtil.SHA256(preHash + strUser + strTx + nonce);
    }

    /**
     * 计算交易的哈希值
     * @param curTx 需要计算的交易
     * @return 当前交易的哈希值
     */
    public static String calcTxHash(Transaction curTx) {
        return CryptoUtil.SHA256(curTx.toString());
    }
}
