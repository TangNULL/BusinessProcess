package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.*;
import com.example.demo.service.ConsortiumBlockchainService;
import com.example.demo.service.BlockService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockchainUtil;
import com.example.demo.utils.CooperationUtil;
import com.example.demo.utils.NetworkUtil;
import com.example.demo.websocket.ConsortiumBPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsortiumBlockchainServiceImpl implements ConsortiumBlockchainService {
    @Autowired
    BlockService blockService;

    @Autowired
    LocalPublicBlockchain localPublicBlockchain;

    @Autowired
    LocalCooperation localCooperation;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    ConsortiumBPClient consortiumBPClient;

    @Override
    public boolean downloadPhase(Integer bpId, Integer senderId, Integer receiverId, String tranDescription) {
        //向公有链发起状态申请
        if(isValidUser(senderId) && isValidUser(receiverId)) {
            //首先添加自己的事件
            ConsortiumBlock consortiumBlock = new ConsortiumBlock(bpId);
            Transaction coopTx = new Transaction(bpId, senderId, receiverId, tranDescription);
            coopTx.setTransId(1);
            if(consortiumBlock.addUser(CooperationUtil.OUTPUT_USER, coopTx.getTransId(), new User(receiverId))
                    && consortiumBlock.addTx(CooperationUtil.FIRST_TX, CooperationUtil.USELESS_USER_ID, coopTx)) {
                //向其他协作者发起协作请求
                String receiverAddress = NetworkUtil.getAddressById(receiverId);
                if(receiverAddress == null) {
                    return false;
                }
                consortiumBPClient.init(receiverAddress, applyForCooperation(coopTx));
                //合作信息保存在本地
                localCooperation.addCooperation(consortiumBlock);
                return true;
            }
        }
        return false;
    }

    /**
     * 从公有链获取用户信息，判断是否合法用户
     * @param userId 输入的用户id
     * @return 返回真实用户是否存在
     */
    private boolean isValidUser(Integer userId) {
        for (User user : localPublicBlockchain.getUsers()) {
            if (user.getUserid().equals(userId)) {
                return true;
            }
        }
        //这里可以添加查询不到就向其他节点广播的功能
        return false;
    }

    @Override
    public boolean generatePhase(Transaction preCoopTx, Integer bpId, Integer selfId, Integer nextId, String tranDescription) {
        //前端确认合作之后触发
        //首先添加自己的事件
        ConsortiumBlock consortiumBlock = localCooperation.getLocalConsortiumChain().get(bpId);

        //添加自己和自己的前置节点，用用户指针将区块连接在一起，每一个区块对应一个用户的业务流程
        if(consortiumBlock.addUser(CooperationUtil.INPUT_USER, preCoopTx.getTransId(), new User(preCoopTx.getSenderId()))
                && consortiumBlock.addTx(CooperationUtil.INPUT_TX, CooperationUtil.USELESS_USER_ID, preCoopTx)) {

            //前向回复，后向传播
            consortiumBPClient.init(NetworkUtil.getAddressById(preCoopTx.getSenderId()),
                    confirmCooperation(preCoopTx));
            //这里还需要收到前置节点确认收到的确认回复，不过暂时不实现了

            //说明无后续节点，这里做完了直接业务流程就完事了
            if (nextId.equals(CooperationUtil.FINISH_USER_ID)) {
                Transaction lastTx = new Transaction(bpId, selfId, nextId, tranDescription);
                //TODO: 本地节点的事情做完之后发送给前置节点
                //需要在本节点的任务完成后触发按键设置最后一个tx为可上传的状态，但是这里简化一下操作，直接变成可上传状态，以作测试之用
                lastTx.setTransState(CooperationUtil.CONFIRM_APPLY_STATE);
                consortiumBlock.addTx(CooperationUtil.LAST_TX, preCoopTx.getTransId(), lastTx);
                //合作信息保存在本地
                localCooperation.addCooperation(consortiumBlock);

                //通知前置节点
                handleApplyForUpLoad(JSON.toJSONString(lastTx));
                return true;
            } else {
                //向其他节点发起协作申请
                Transaction coopTx = new Transaction(bpId, selfId, nextId, tranDescription);
                consortiumBlock.addTx(CooperationUtil.OUTPUT_TX, preCoopTx.getTransId(), coopTx);
                //合作信息保存在本地
                localCooperation.addCooperation(consortiumBlock);
                if (consortiumBlock.addUser(CooperationUtil.OUTPUT_USER, coopTx.getTransId(), new User(nextId))
                        && consortiumBlock.addTx(CooperationUtil.OUTPUT_TX, preCoopTx.getTransId(), coopTx)) {
                    String receiverAddress = NetworkUtil.getAddressById(nextId);
                    if(receiverAddress == null) {
                        return false;
                    }
                    consortiumBPClient.init(receiverAddress, applyForCooperation(coopTx));
                    return true;
                }
            }
        }
        return false;
    }

//    @Override
//    public boolean uploadPhase(Transaction curCoopTx) {
////        //双向哈希验证算法，每一步双向传播确认信息真实性
////         switch (curCoopTx.getTransState()) {
////             case CooperationUtil.APPLY_STATE:
////                 //如果自己是未通过合作的状态
////                 System.out.println("合作请求未通过，返回错误！");
////                 return false;
////             case CooperationUtil.CONFIRM_APPLY_STATE:
////                //如果该合作处于传到下一个状态，则前向传递通知已完成
////                //前向传播通知前序节点，同步区块消息
////                String receiverAddress = NetworkUtil.getAddressById(curCoopTx.getSenderId());
////                if(receiverAddress == null) {
////                    return false;
////                }
////                consortiumBPClient.init(receiverAddress, applyForUpload(curCoopTx));
////                //如果修改报错，就用迭代器
////
////                return true;
////             case CooperationUtil.CONFIRM_UPLOAD_STATE:
////                //后向传播，数据上传公有链
////                //把本地交易上传到联盟链
////                //上传的时候第一个用户上传全部的交易数据，然后发送给其他用户检验，确认无误后向全网广播
////
////                //需要经过两次双向传递，每个节点之间经过三次握手之后，第一个节点用户才能获得全部的交易数据
////                //第一个节点把全体的数据上链，但是需要经过其他所有节点的验证
////                localPublicBlockchain.addTxCache(localCooperation.getLocalConsortiumChain().get(curCoopTx.getBpId()).getOutputTxs().values());
////                return removeConsBlock(curCoopTx.getBpId());
////            case 3:
////                break;
////         }
//        return true;
//    }

    /**
     * 协作结束后销毁联盟区块
     * @param removeBpId 需要销毁的业务流程id
     * @return 销毁是否成功
     */
    private boolean removeConsBlock(Integer removeBpId) {
        for (Integer bpId : localCooperation.getLocalConsortiumChain().keySet()) {
            if (removeBpId.equals(bpId)) {
                localCooperation.getLocalConsortiumChain().remove(removeBpId);
                return true;
            }
        }
        return false;
    }

    @Override
    public String applyForCooperation(Transaction tx){
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.APPLY_FOR_COOPERATION);
        msg.setData(JSON.toJSONString(tx));
        return JSON.toJSONString(msg);
    }


    @Override
    public String confirmCooperation(Transaction tx) {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.CONFIRM_FOR_COOPERATION);
        msg.setData(JSON.toJSONString(tx));
        return JSON.toJSONString(msg);
    }

    @Override
    public String applyForUpload(Transaction tx) {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.APPLY_FOR_UPLOAD);
        msg.setData(JSON.toJSONString(tx));
        return JSON.toJSONString(msg);
    }

    @Override
    public String confirmForUpLoad(Transaction tx) {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.CONFIRM_FOR_UPLOAD);
        msg.setData(JSON.toJSONString(tx));
        return JSON.toJSONString(msg);
    }

    @Override
    public void handleApplyForCooperation(String bpData) {
        //反序列化得到其它节点传来的协作流程
        Transaction receivedTx = JSON.parseObject(bpData, Transaction.class);
        localCooperation.getLocalConsortiumChain().get(receivedTx.getBpId()).addTx(CooperationUtil.INPUT_TX, CooperationUtil.USELESS_USER_ID, receivedTx);
        ConsortiumBlock consortiumBlock;
        if (localCooperation.getLocalConsortiumChain().containsKey(receivedTx.getBpId())) {
            consortiumBlock = localCooperation.getLocalConsortiumChain().get(receivedTx.getBpId());
        } else {
            consortiumBlock = new ConsortiumBlock(receivedTx.getBpId());
        }
        //TODO: 在接收节点前端展示接收到的合作请求，保存在消息队列中
        //确认合作之后保存在本地
        localCooperation.getLocalConsortiumChain().put(receivedTx.getBpId(), consortiumBlock);
    }

    @Override
    public void handleConfirmCooperation(NetworkMsg msg) {
        //反序列化得到其它节点的返回是否同意合作
        switch (msg.getType()) {
            //同意合作
            case BlockchainUtil.CONFIRM_FOR_COOPERATION:
                Transaction responseTx = JSON.parseObject(msg.getData(), Transaction.class);
                if (responseTx.getSenderId().equals(localCooperation.getLocalUser().getUserid())) {
                    ConsortiumBlock localConsortiumBlock = localCooperation.getLocalConsortiumChain().get(responseTx.getBpId());
                    if (localConsortiumBlock.getOutputTxs().get(responseTx.getTransId()).getTransState().equals(CooperationUtil.APPLY_STATE)) {
                        localConsortiumBlock.getOutputTxs().get(responseTx.getTransId()).setTransState(CooperationUtil.CONFIRM_APPLY_STATE);
                    } else {
                        System.out.println("返回的请求有误！");
                    }

                } else {
                    System.out.println("返回的同意请求不是本用户发出的！");
                }
                break;
            //拒绝合作,先不管
            case BlockchainUtil.APPLY_DENY:
                break;
        }
    }

    @Override
    public void handleApplyForUpLoad(String bpData) {
        //反序列化得到其它节点传来的协作流程
        Transaction receivedTx = JSON.parseObject(bpData, Transaction.class);
        ConsortiumBlock localConsortiumBlock = localCooperation.getLocalConsortiumChain().get(receivedTx.getBpId());

        //将本地的节点标记成为可上传的状态，并且向前发送
        if (localConsortiumBlock.getOutputTxs().containsKey(receivedTx.getTransId())) {
            switch (localConsortiumBlock.getOutputTxs().get(receivedTx.getTransId()).getTransState()) {
                case CooperationUtil.APPLY_STATE:
                    //如果自己是未通过合作的状态
                    System.out.println("合作请求未通过，返回错误！");
                    break;
                case CooperationUtil.CONFIRM_APPLY_STATE:
                    //如果该合作处于传到下一个状态，则通过返回的申请上传请求，表示后继节点已经完成，并且将自己也标记成可以上传的状态
                    localConsortiumBlock.getOutputTxs().get(receivedTx.getTransId()).setTransState(CooperationUtil.UPLOAD_STATE);
                    //向前发送完成任务的请求
                    Integer preTxId = localConsortiumBlock.addFinishedOutputTxs(receivedTx);
                    if (!preTxId.equals(CooperationUtil.ERROR_USER_ID)) {
                        if (preTxId.equals(CooperationUtil.FIRST_TX)) {
                            //说明到了发起合作的用户的事件，不用通知别人了
                            if (localConsortiumBlock.isFinished()) {
                                //如果当前节点全部事情都做完了，那么向后轮询是否其他节点都做完了
                                String receiverAddress = NetworkUtil.getAddressById(receivedTx.getReceiverId());
                                consortiumBPClient.init(receiverAddress, confirmForUpLoad(receivedTx));

                            }
                            //TODO: 前端展示
                        } else {
                            Transaction preTx = localConsortiumBlock.getInputTxs().get(preTxId);
                            String receiverAddress = NetworkUtil.getAddressById(preTx.getSenderId());
                            consortiumBPClient.init(receiverAddress, applyForUpload(preTx));
                        }
                    }
                    break;
                case CooperationUtil.UPLOAD_STATE:
                    //如果自己已经是可上传的状态，说明发生错误
                    System.out.println("已经进入上传状态！");
                    break;
                case CooperationUtil.CONFIRM_UPLOAD_STATE:
                    //如果自己已经是上传完成，说明发生错误
                    System.out.println("已经上传完数据，返回错误！");
                    break;
            }
        } else {
            System.out.println("交易不存在");
        }
    }

    @Override
    public void handleConfirmForUpLoad(String bpData) {

    }

    @Override
    public String applyDeny() {
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.APPLY_DENY));
    }
}