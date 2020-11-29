package com.example.demo.entity;

public class LatestTransaction {
    private int bpId;
    private String tranDescription;
    private String hash;
    private int sender;
    private int receiver;
    private String time;

    public LatestTransaction(int bpId, String tranDescription, String hash, int sender, int receiver, String time) {
        this.bpId = bpId;
        this.tranDescription = tranDescription;
        this.hash = hash;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
    }

    public int getBpId() {
        return bpId;
    }

    public void setBpId(int bpId) {
        this.bpId = bpId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTranDescription() {
        return tranDescription;
    }

    public void setTranDescription(String tranDescription) {
        this.tranDescription = tranDescription;
    }
}
