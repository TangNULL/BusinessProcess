package com.example.demo.service;

import com.example.demo.entity.NetworkMsg;
import com.example.demo.entity.Transaction;
import org.java_websocket.WebSocket;

import java.util.List;

/**
 * 生成联盟链并且从联盟链上传记录的数据
 */
public interface ConsortiumBlockchainService{

    //联盟链生命周期的三个阶段
    boolean downloadPhase(Integer bpId, Integer senderId, Integer receiverId, String tranDescription);  //生成联盟链
    boolean generatePhase(Transaction preCoopTx, Integer bpId, Integer selfId, Integer nextId, String tranDescription);  //联盟链扩展
//    boolean uploadPhase(Transaction curCoopTx);  //联盟链上传

    //联盟链传递的消息
    String applyForCooperation(Transaction tx);  //申请协作
    String confirmCooperation(Transaction tx);  //响应协作
    String applyForUpload(Transaction tx);  //申请上传数据
    String confirmForUpLoad(Transaction tx);  //同意返回数据

    //处理联盟链传递的消息
    void handleApplyForCooperation(String bpData);  //处理申请协作请求
    void handleConfirmCooperation(NetworkMsg msg);  //处理响应协作请求
    void handleApplyForUpLoad(String bpData);  //处理上传数据请求
    void handleConfirmForUpLoad(String bpData);  //处理响应上传数据请求
    String applyDeny();  //申请不通过
}
