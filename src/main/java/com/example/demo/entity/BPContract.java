package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.List;

public class BPContract {  //业务流程合约 即一次合作
    private Integer contractId;
    //private String hash;  //由下面计算出的hash
    private Integer bpSenderId;  //合作发起方   user 还是  userID就够了？？？？？
    private Integer bpReceiverId;  //合作接收方   user 还是  userID就够了？？？？？
    private String bpDescription;  //合作内容
    private List<Transaction> transactionList;  //（交易内容、是否完成、协商历史  json字符串 放在了transaction里面）
    private Boolean isReceiverAccepted;  // 接收方接受合同

     /*private Boolean isSenderAck;  // 发起方确认完成合同
    private Boolean isReceiverAck;  // 接收方确认完成合同*/

    public BPContract(Integer bpSenderId, Integer bpReceiverId, String bpDescription) {
        this.bpSenderId = bpSenderId;
        this.bpReceiverId = bpReceiverId;
        this.bpDescription = bpDescription;
    }


    public BPContract(Integer contractId, Integer bpSenderId, Integer bpReceiverId, String bpDescription, int isReceiverAccepted) {
        this.contractId = contractId;
        this.bpSenderId = bpSenderId;
        this.bpReceiverId = bpReceiverId;
        this.bpDescription = bpDescription;
        this.isReceiverAccepted = isReceiverAccepted == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public BPContract() {
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getBpSenderId() {
        return bpSenderId;
    }

    public void setBpSenderId(Integer bpSenderId) {
        this.bpSenderId = bpSenderId;
    }

    public Integer getBpReceiverId() {
        return bpReceiverId;
    }

    public void setBpReceiverId(Integer bpReceiverId) {
        this.bpReceiverId = bpReceiverId;
    }

    public String getBpDescription() {
        return bpDescription;
    }

    public void setBpDescription(String bpDescription) {
        this.bpDescription = bpDescription;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Boolean getReceiverAccepted() {
        return isReceiverAccepted;
    }

    public void setReceiverAccepted(Boolean receiverAccepted) {
        isReceiverAccepted = receiverAccepted;
    }
}
