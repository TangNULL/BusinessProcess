package com.example.demo.entity;


import com.example.demo.utils.CooperationUtil;
import com.example.demo.utils.CryptoUtil;

import java.sql.Timestamp;

public class Transaction {
    private Integer bpId;  //流程实例id
    private Integer transId;  //当前tx的标识id
    private String hash;  //由发起方+接收方+时间戳+交易内容+协商历史计算出的hash
    private Integer senderId;  //交易发起方     user 还是  userID就够了？？？？？
    private Integer receiverId;  //交易接收方   user 还是  userID就够了？？？？？
    private Timestamp createTime;  //发起时间
    private Timestamp completeTime;  //完成时间
    private String tranDescription;  //交易内容
    private Boolean isSenderAck;  // 发起方确认完成transaction
    private Boolean isReceiverAck;  // 接收方确认完成transaction
    private String consultation;  //协商历史
    private Integer transState;  //协作所处的状态
    private String preHash;
    private String nextHash;

    // 方便测试的构造方法，实际使用不到
    public Transaction(Integer bpId, Integer senderId, Integer receiverId, String tranDescription) {
        this.bpId = bpId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.tranDescription = tranDescription;
        this.transState = CooperationUtil.APPLY_STATE;
    }

    public Transaction(Transaction t) {
        this.bpId = t.getBpId();
        this.transId = t.getTransId();
        this.hash = t.getHash();
        this.senderId = t.getSenderId();
        this.receiverId = t.getReceiverId();
        this.createTime = t.getCreateTime();
        this.completeTime = t.getCompleteTime();
        this.tranDescription = t.getTranDescription();
        this.isSenderAck = t.getSenderAck();
        this.isReceiverAck = t.getReceiverAck();
        this.consultation = t.getConsultation();
    }

    public Transaction(Integer bpId, Integer transId, String hash, Integer senderId, Integer receiverId, Timestamp createTime, Timestamp completeTime, String tranDescription, Boolean isSenderAck, Boolean isReceiverAck, String consultation) {
        this.bpId = bpId;
        this.transId = transId;
        this.hash = hash;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createTime = createTime;
        this.completeTime = completeTime;
        this.tranDescription = tranDescription;
        this.isSenderAck = isSenderAck;
        this.isReceiverAck = isReceiverAck;
        this.consultation = consultation;
    }

    public Transaction(Integer senderId, Integer receiverId, String tranDescription) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.tranDescription = tranDescription;
    }

    public Transaction() {
    }

    public Integer getBpId() {
        return bpId;
    }

    public void setBpId(Integer bpId) {
        this.bpId = bpId;
    }

    public Integer getTransId() {
        return transId;
    }

    public void setTransId(Integer transId) {
        this.transId = transId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setHash() {
        this.hash = CryptoUtil.SHA256(this.getSenderId() + "" + this.getReceiverId() + this.getCreateTime() + this.getCompleteTime() + this.getTranDescription());
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Timestamp completeTime) {
        this.completeTime = completeTime;
    }

    public String getTranDescription() {
        return tranDescription;
    }

    public void setTranDescription(String tranDescription) {
        this.tranDescription = tranDescription;
    }

    public Boolean getSenderAck() {
        return isSenderAck;
    }

    public void setSenderAck(Boolean senderAck) {
        isSenderAck = senderAck;
    }

    public Boolean getReceiverAck() {
        return isReceiverAck;
    }

    public void setReceiverAck(Boolean receiverAck) {
        isReceiverAck = receiverAck;
    }

    public String getConsultation() {
        return consultation;
    }

    public void setConsultation(String consultation) {
        this.consultation = consultation;
    }

    public Integer getTransState() {
        return transState;
    }

    public void setTransState(Integer transState) {
        this.transState = transState;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public String getNextHash() {
        return nextHash;
    }

    public void setNextHash(String nextHash) {
        this.nextHash = nextHash;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "bpId=" + bpId +
                ", transId=" + transId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", createTime=" + createTime +
                ", completeTime=" + completeTime +
                ", tranDescription='" + tranDescription + '\'' +
                ", transState=" + transState +
                ", preHash='" + preHash + '\'' +
                '}';
    }
}
