package com.example.demo.entity;

import org.java_websocket.WebSocket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当前节点保存的联盟链数据
 */
@Component
public class LocalCooperation {

    private Map<Integer, ConsortiumBlock> localConsortiumChain;  //保存本地所有的合作信息，Map<协作业务流程的bpId，流程实例>
    private Map<Integer, List<Transaction>> localTxcBP;  //某个业务流程对应的全部交易信息，Map<bpId，List<Txs>>
    private String address;  //本机ip地址
    private int port;  //本地端口号
    private User localUser;  //本地节点对应的用户

    public LocalCooperation() {
        this.localConsortiumChain = new HashMap<>();
        this.localTxcBP = new HashMap<>();
    }

    public Map<Integer, ConsortiumBlock> getLocalConsortiumChain() {
        return localConsortiumChain;
    }

    public void setLocalConsortiumChain(Map<Integer, ConsortiumBlock> localConsortiumChain) {
        this.localConsortiumChain = localConsortiumChain;
    }

    public Map<Integer, List<Transaction>> getLocalTxcBP() {
        return localTxcBP;
    }

    public void setLocalTxcBP(Map<Integer, List<Transaction>> localTxcBP) {
        this.localTxcBP = localTxcBP;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public User getLocalUser() {
        return localUser;
    }

    public void setLocalUser(User localUser) {
        this.localUser = localUser;
    }

    public boolean  addCooperation(ConsortiumBlock consortiumBlock) {
        if (localConsortiumChain.containsKey(consortiumBlock.getBpId())) {
            return false;
        } else {
            localConsortiumChain.put(consortiumBlock.getBpId(), consortiumBlock);
            return true;
        }
    }

    public boolean removeCooperation(ConsortiumBlock consortiumBlock) {
        if (localConsortiumChain.containsKey(consortiumBlock.getBpId())) {
            return false;
        } else {
            localConsortiumChain.put(consortiumBlock.getBpId(), consortiumBlock);
            return true;
        }
    }
}