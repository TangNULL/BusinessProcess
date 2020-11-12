package com.example.demo.entity;

public class Transaction {
    private int transId;  //
    private User sender;  //交易发起方
    private User receiver;  //交易接收方
    private String tranDescription;  //交易内容
    private boolean isComplete;  //交易是否完成
    private String consultation;  //协商历史

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getTranDescription() {
        return tranDescription;
    }

    public void setTranDescription(String tranDescription) {
        this.tranDescription = tranDescription;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getConsultation() {
        return consultation;
    }

    public void setConsultation(String consultation) {
        this.consultation = consultation;
    }
}
