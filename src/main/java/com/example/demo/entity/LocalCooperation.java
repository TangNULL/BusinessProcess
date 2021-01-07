package com.example.demo.entity;

import com.example.demo.mapper.BlockMapper;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 当前节点保存的联盟链数据
 */
@Component
public class LocalCooperation {



    private Map<Integer, ConsortiumBlock> localConsortiumChain;  //保存本地所有的合作信息，Map<协作业务流程的bpId，流程实例>
    private Map<Integer, List<Transaction>> localTxs;  //某个业务流程对应的全部交易信息，Map<bpId，List<Txs>>
    private String address;  //本机ip地址
    private int port;  //本地端口号
    private User localUser;  //本地节点对应的用户

    public LocalCooperation() {
        this.localConsortiumChain = new HashMap<>();
        this.localTxs = new HashMap<>();
    }

    public void init(List<Transaction> inputTxsList, List<Transaction> outputTxsList) {
        List<Transaction> txsList = new ArrayList<>(inputTxsList);
        txsList.addAll(outputTxsList);
        for (Transaction tx : txsList) {
            if (localTxs.isEmpty() || !localTxs.containsKey(tx.getBpId())) {
                List<Transaction> tempTxs = new ArrayList<>();
                tempTxs.add(tx);
                localTxs.put(tx.getBpId(), tempTxs);
            } else {
                localTxs.get(tx.getBpId()).add(tx);
            }
        }
        for (Map.Entry<Integer, List<Transaction>> txs : localTxs.entrySet()) {
            ConsortiumBlock tempConsBlock = new ConsortiumBlock(txs.getKey(), localUser.getUserid(), txs.getValue());
            localConsortiumChain.put(txs.getKey(), tempConsBlock);
        }
    }

    public Map<Integer, ConsortiumBlock> getLocalConsortiumChain() {
        return localConsortiumChain;
    }

    public void setLocalConsortiumChain(Map<Integer, ConsortiumBlock> localConsortiumChain) {
        this.localConsortiumChain = localConsortiumChain;
    }

    public Map<Integer, List<Transaction>> getLocalTxs() {
        return localTxs;
    }

    public void setLocalTxs(Map<Integer, List<Transaction>> localTxs) {
        this.localTxs = localTxs;
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