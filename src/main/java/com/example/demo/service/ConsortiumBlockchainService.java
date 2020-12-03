package com.example.demo.service;

import org.java_websocket.WebSocket;

import java.util.List;

/**
 * 生成联盟链并且从联盟链上传记录的数据
 */
public interface ConsortiumBlockchainService{
    boolean downloadPhase(Integer transId, Integer senderId, Integer receiverId, String tranDescription);  //生成联盟链
    boolean generatePhase();  //联盟链扩展
    boolean uploadPhase();  //联盟链上传
    //联盟链传递的消息
    String applyForCooperation();  //申请其他参与者协作
    String confirmCooperation();  //同意参与合作
    String applyDeny();  //申请不通过
}
