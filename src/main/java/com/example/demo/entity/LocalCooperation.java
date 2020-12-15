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

    private Map<Integer, ConsortiumBlock> localTotalCBP = new HashMap<>();  //保存本地所有的合作信息 Map<协作业务流程的bpId，流程实例>
    private String address;  //本机ip地址
    private int port;  //本地端口号
    private User localUser;  //本地节点对应的用户

    public Map<Integer, ConsortiumBlock> getLocalConsortiumChain() {
        return localTotalCBP;
    }

    public void setLocalConsortiumChain(Map<Integer, ConsortiumBlock> localTotalCBP) {
        this.localTotalCBP = localTotalCBP;
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
        if (localTotalCBP.containsKey(consortiumBlock.getBpId())) {
            return false;
        } else {
            localTotalCBP.put(consortiumBlock.getBpId(), consortiumBlock);
            return true;
        }
    }

    public boolean removeCooperation(ConsortiumBlock consortiumBlock) {
        if (localTotalCBP.containsKey(consortiumBlock.getBpId())) {
            return false;
        } else {
            localTotalCBP.put(consortiumBlock.getBpId(), consortiumBlock);
            return true;
        }
    }
}