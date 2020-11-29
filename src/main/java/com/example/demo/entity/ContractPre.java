package com.example.demo.entity;

import java.util.List;

public class ContractPre {
    private Integer contractId;
    private String bpSenderName;  //合作发起方
    private String bpReceiverName;  //合作接收方
    private String bpDescription;  //合作内容
    private List<String> transactionJsonList;  //（交易内容、是否完成、协商历史  json字符串 放在了transaction里面）
    private Boolean isReceiverAccepted;  // 接收方接受合同

    public ContractPre(Integer contractId, String bpSenderName, String bpReceiverName, String bpDescription, List<String> transactionJsonList, Boolean isReceiverAccepted) {
        this.contractId = contractId;
        this.bpSenderName = bpSenderName;
        this.bpReceiverName = bpReceiverName;
        this.bpDescription = bpDescription;
        this.transactionJsonList = transactionJsonList;
        this.isReceiverAccepted = isReceiverAccepted;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public String getBpSenderName() {
        return bpSenderName;
    }

    public void setBpSenderName(String bpSenderName) {
        this.bpSenderName = bpSenderName;
    }

    public String getBpReceiverName() {
        return bpReceiverName;
    }

    public void setBpReceiverName(String bpReceiverName) {
        this.bpReceiverName = bpReceiverName;
    }

    public String getBpDescription() {
        return bpDescription;
    }

    public void setBpDescription(String bpDescription) {
        this.bpDescription = bpDescription;
    }

    public List<String> getTransactionJsonList() {
        return transactionJsonList;
    }

    public void setTransactionJsonList(List<String> transactionJsonList) {
        this.transactionJsonList = transactionJsonList;
    }

    public Boolean getReceiverAccepted() {
        return isReceiverAccepted;
    }

    public void setReceiverAccepted(Boolean receiverAccepted) {
        isReceiverAccepted = receiverAccepted;
    }
}
