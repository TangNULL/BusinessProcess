package com.example.demo.entity;

import java.sql.Timestamp;

public class BusinessProcessPre {
    private Integer bpId;
    private String users;
    private Timestamp createTime;
    private Timestamp completeTime;
    private String state;

    public BusinessProcessPre(Integer bpId, String users, Timestamp createTime, Timestamp completeTime, String state) {
        this.bpId = bpId;
        this.users = users;
        this.createTime = createTime;
        this.completeTime = completeTime;
        this.state = state;
    }

    public Integer getBpId() {
        return bpId;
    }

    public void setBpId(Integer bpId) {
        this.bpId = bpId;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
