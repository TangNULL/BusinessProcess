package com.example.demo.entity;

import java.util.List;

public class BusinessProcess {  //每个业务流程包含若干合约，每个合约对应一次合作，每个合作包含若干transaction（交易）
    private int bpId;  //业务流程ID
    private List<User> userList;  //参与组织列表
    private List<BPContract> bpContractList;  //合约列表
    private String blockId;  //所在区块ID

    public int getBpId() {
        return bpId;
    }

    public void setBpId(int bpId) {
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
}
