package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.List;

public class BPContract {  //业务流程合约 即一次合作
    private int contractId;
    private String hash;  //由下面计算出的hash
    private User BPSender;  //合作发起方
    private User BPReceiver;  //合作接收方
    private Timestamp createTime;  //合作发起时间
    private String BPDescription;  //合作内容
    private List<Transaction> transactionList;  //（交易内容、是否完成、协商历史  json字符串 放在了transaction里面）
    private String cooperationTime;  //合作时长
    private boolean isComplete;  //合作是否完成

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public User getBPSender() {
        return BPSender;
    }

    public void setBPSender(User BPSender) {
        this.BPSender = BPSender;
    }

    public User getBPReceiver() {
        return BPReceiver;
    }

    public void setBPReceiver(User BPReceiver) {
        this.BPReceiver = BPReceiver;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getBPDescription() {
        return BPDescription;
    }

    public void setBPDescription(String BPDescription) {
        this.BPDescription = BPDescription;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public String getCooperationTime() {
        return cooperationTime;
    }

    public void setCooperationTime(String cooperationTime) {
        this.cooperationTime = cooperationTime;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
