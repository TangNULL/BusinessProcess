package com.example.demo.entity;

public class Transaction {
    private String tId;  //
    private User sender;  //交易发起方
    private User receiver;  //交易接收方
    private String TranDescription;  //交易内容
    private boolean isComplete;  //交易是否完成
    private String Consultation;  //协商历史

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
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
        return TranDescription;
    }

    public void setTranDescription(String tranDescription) {
        TranDescription = tranDescription;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getConsultation() {
        return Consultation;
    }

    public void setConsultation(String consultation) {
        Consultation = consultation;
    }
}
