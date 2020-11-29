package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.List;

public class BusinessProcess {  //每个业务流程包含若干合约，每个合约对应一次合作，每个合作包含若干transaction（交易）
    private Integer bpId;  //业务流程ID
    private List<User> userList;  //参与组织列表
    private List<BPContract> bpContractList;  //合约列表
    private String blockId;  //所在区块ID
    private String ackUsers;  //参与者中确认自己的流程已结束的userId列表 当列表长度=userList.size()时认为流程已结束
    private Timestamp createTime;
    private Timestamp completeTime;

    public Integer getBpId() {
        return bpId;
    }

    public void setBpId(Integer bpId) {
        this.bpId = bpId;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<BPContract> getBpContractList() {
        return bpContractList;
    }

    public void setBpContractList(List<BPContract> bpContractList) {
        this.bpContractList = bpContractList;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getAckUsers() {
        return ackUsers;
    }

    public void setAckUsers(String ackUsers) {
        this.ackUsers = ackUsers;
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
}
