package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.*;
import com.example.demo.service.ConsortiumBlockchainService;
import com.example.demo.service.BlockService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockchainUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsortiumBlockchainServiceImpl implements ConsortiumBlockchainService {
    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain localPublicBlockchain;

    @Autowired
    LocalConsortiumBlockchain localConsortiumBlockchain;

    @Autowired
    WebSocketService webSocketService;

    @Override
    public boolean downloadPhase(Integer transId, Integer senderId, Integer receiverId, String tranDescription) {
        //向公有链发起状态申请
        if(isUserExist(senderId) && isUserExist(receiverId)) {
            //首先添加自己的事件
            addUser(senderId);
            addTx(transId, senderId, receiverId, tranDescription);
            NetworkMsg networkMsg = new NetworkMsg(BlockchainUtil.APPLY_FOR_COOPERATION, tranDescription);
            return true;
        }
        return false;
    }

    private boolean isUserExist(Integer userId) {
        for (User user : localPublicBlockchain.getUsers()) {
            if (user.getUserid() == userId) {
                return true;
            }
        }
        //这里可以添加查询不到就向其他节点广播的功能
        return false;
    }

    private void addUser(Integer senderId){
        localConsortiumBlockchain.getUsers().add(new User(senderId));
    }

    private void addTx(Integer transId, Integer senderId, Integer receiverId, String tranDescription){
        localConsortiumBlockchain.getTxs().add(new Transaction(transId, senderId, receiverId, tranDescription));
    }

    //先放着，暂时不做
    private void deleteUser(Integer senderId) {
        for (User user : localConsortiumBlockchain.getUsers()) {
            if (user.getUserid() == senderId) {
                localConsortiumBlockchain.getUsers().remove(user);
                break;
            }
        }
    }

    //先放着，暂时不做
    private void deleteTx(Integer transId, Integer senderId, Integer receiverId, String tranDescription) {
        for (Transaction tx : localConsortiumBlockchain.getTxs()) {
            if (tx.getTransId() == transId && tx.getSenderId() == senderId && tx.getReceiverId() == receiverId && tx.getTranDescription() == tranDescription) {
                localConsortiumBlockchain.getTxs().remove(tx);
                break;
            }
        }
    }

    @Override
    public boolean generatePhase() {

        return false;
    }

    @Override
    public boolean uploadPhase() {
        blockService.addTxCache(localConsortiumBlockchain.getTxs());
        deleteConsBlock();

        return true;
    }

    private void deleteConsBlock() {
        //销毁联盟链
        localConsortiumBlockchain.getUsers().clear();
        localConsortiumBlockchain.getTxs().clear();
        localConsortiumBlockchain.getPkHash().clear();
    }

    @Override
    public String applyForCooperation(){
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.APPLY_FOR_COOPERATION));
    }

    @Override
    public String confirmCooperation() {
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.CONFIRM_FOR_COOPERATION));
    }

    @Override
    public String applyDeny() {
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.APPLY_DENY));
    }
}