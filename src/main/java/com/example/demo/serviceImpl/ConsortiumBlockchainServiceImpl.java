package com.example.demo.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.*;
import com.example.demo.service.ConsortiumBlockchainService;
import com.example.demo.service.BlockService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.BlockchainUtil;
import com.example.demo.utils.CooperationUtil;
import com.example.demo.utils.CryptoUtil;
import com.example.demo.utils.NetworkUtil;
import com.example.demo.websocket.ConsortiumBPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
            //第一个事件的初始化
            ConsortiumBlock consortiumBlock = new ConsortiumBlock(bpId);
            Transaction coopTx = new Transaction(bpId, senderId, receiverId, tranDescription);
            coopTx.setTransId(1);
            coopTx.setPreHash("");
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

    /**
     * transId对应的合作outputTx可能有很多个，每个outputTx对应一个唯一的transId
     * @param preCoopTx 前一个事件的transId
     * @param bpId 当前业务流程实例的bpId
     * @param selfId 自己的userId
     * @param nextId 下一个需要合作的userId，如果没有则是 FINISH_USER_ID
     * @param tranDescription 需要做的事情的描述
     * @return 是否发起合作成功
     */

    /**
     * 双向验证算法
     * 每一步双向传播确认信息真实性，需要经过三次双向传递，才能获得全部的交易数据并且上传
     * Step1 head-->tail: 合作从FIRST_TX开始，到LAST_TX结束。每个bpId对应一个流程实例，每个bpId只能有1个FIRST_TX，
     * 但是可以有多个LAST_TX，LAST_TX只表示当前用户的任务做完了且不需要其他人（包括自己）的合作了
     * Step2 tail-->head: LAST_TX任务完成后前向反馈通知前序节点，如果前置合作的所有后继合作都是已完成状态，则前置合作也被标记成已完成，同时继续向前传递消息
     * 这里每个节点向前通知的时候会附带上自己的的已完成任务的哈希值用来在后续步骤校验本地任务没有改动
     * head-->tail: 当到达FIRST_TX的时候，如果第一个任务已经是完成状态，则开始后向传播，通知后续节点准备上传数据，把当前任务标记成可上传状态
     * tail-->head: 当确认传播到达LAST_TX时，数据上传公有链，把本地交易上传到联盟链
     * Step3 head-->tail: 上传的时候                      第一个用户上传全部的交易数据，然后发送给其他用户检验，确认无误后向全网广播
     * tail-->head: 第一个节点把全体的数据上链，但是需要经过其他所有节点的验证
     */
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

    @Override
    public String uploadPhase(ConsortiumBlock cBPBlock) {
        //TODO: 上传数据到公有链上并且同步给其他合作节点，然后向全网广播
        List<Transaction> uploadTxs = new ArrayList<>();
        for (String outputEncryptTx : cBPBlock.getUploadData().get(CooperationUtil.FIRST_TX)) {
            Transaction tx = JSON.parseObject(outputEncryptTx, Transaction.class);
            uploadTxs.add(tx);
        }

        //TODO: 其他节点确认数据的完整性和真实性
        //这里先跳过这个步骤，直接上传到公有链上
        localPublicBlockchain.addTxCache(uploadTxs);

        return JSON.toJSONString(uploadTxs);
    }

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
    public String confirmForUpload(Transaction tx) {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.CONFIRM_FOR_UPLOAD);
        msg.setData(JSON.toJSONString(tx));
        return JSON.toJSONString(msg);
    }

    @Override
    public String uploadData(Transaction preTx, String curEncryptTx) {
        NetworkMsg msg = new NetworkMsg();
        msg.setType(BlockchainUtil.UPLOAD_DATA);
        msg.setData(JSON.toJSONString(preTx)+ "$$" + curEncryptTx);
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
                case CooperationUtil.CONFIRM_APPLY_STATE:
                    //如果该合作处于UPLOAD_STATE，则通过返回的申请上传请求，表示该后继节点已经完成
                    localConsortiumBlock.getOutputTxs().get(receivedTx.getTransId()).setTransState(CooperationUtil.UPLOAD_STATE);
                    //向前发送完成任务的请求
                    Integer preTxId = localConsortiumBlock.addFinishedOutputTxs(receivedTx);
                    if (!preTxId.equals(CooperationUtil.ERROR_USER_ID)) {
                        if (localConsortiumBlock.isFinished(preTxId)) {
                            //当前输入任务的全部后置任务已完成，将自己也标记成可以上传的状态
                            localConsortiumBlock.getInputTxs().get(preTxId).setTransState(CooperationUtil.UPLOAD_STATE);
                            if (preTxId.equals(CooperationUtil.FIRST_TX)) {
                                //说明到了发起合作的用户的事件，不用通知别人了
                                Transaction firstTx = localConsortiumBlock.getOutputTxs().get(preTxId);
                                firstTx.setTransState(CooperationUtil.CONFIRM_UPLOAD_STATE);
                                firstTx.setHash(CryptoUtil.calcTxHash(localConsortiumBlock.getOutputTxs().get(preTxId)));
                                String receiverAddress = NetworkUtil.getAddressById(firstTx.getReceiverId());
                                consortiumBPClient.init(receiverAddress, confirmForUpload(firstTx));

                                //如果当前节点是第一个节点用户，它的全部事情都做完了，那么直接上传数据
//                            if (localConsortiumBlock.isFinished()) {
//                                for (Transaction tx : localConsortiumBlock.getOutputTxs().values()) {
//                                    String receiverAddress = NetworkUtil.getAddressById(tx.getReceiverId());
//                                    consortiumBPClient.init(receiverAddress, confirmForUpLoad(tx));
//                                }
//                            }

                                //TODO: 前端展示当前任务已经变成可上传的状态
                            } else {
                                Transaction preTx = localConsortiumBlock.getInputTxs().get(preTxId);
                                String receiverAddress = NetworkUtil.getAddressById(preTx.getSenderId());
                                consortiumBPClient.init(receiverAddress, applyForUpload(preTx));
                            }
                        }

                    }
                    break;
//                case CooperationUtil.APPLY_STATE:
//                    //如果自己是未通过合作的状态
//                    System.out.println("合作请求未通过，返回错误！");
//                    break;
//                case CooperationUtil.UPLOAD_STATE:
//                    //如果自己已经是可上传的状态，说明发生错误
//                    System.out.println("已经进入上传状态！");
//                    break;
//                case CooperationUtil.CONFIRM_UPLOAD_STATE:
//                    //如果自己已经是上传完成，说明发生错误
//                    System.out.println("已经上传完数据，返回错误！");
//                    break;
                default:
                    System.out.println("当前交易的状态错误！");
                    break;
            }
        } else {
            System.out.println("交易不存在");
        }
    }

    @Override
    public void handleConfirmForUpLoad(String bpData) {
        //反序列化得到其它节点传来的协作流程
        Transaction receivedTx = JSON.parseObject(bpData, Transaction.class);
        ConsortiumBlock localConsortiumBlock = localCooperation.getLocalConsortiumChain().get(receivedTx.getBpId());

        //将本地的节点的数据加密后后向传播
        if (localConsortiumBlock.getInputTxs().containsKey(receivedTx.getTransId())) {
            //对本地节点上对应传入的前序节点的所有后继节点遍历传递可以上传信号
            for (Integer outputTxId : localConsortiumBlock.getInputs2Outputs().get(receivedTx.getTransId())) {
                Transaction correspondingOutputTx = localConsortiumBlock.getOutputTxs().get(outputTxId);

                switch (correspondingOutputTx.getTransState()) {
                    //后向传播哈希值确认区块信息
                    case CooperationUtil.UPLOAD_STATE:
                        if (receivedTx.getTransState().equals(CooperationUtil.CONFIRM_UPLOAD_STATE)) {
                            correspondingOutputTx.setTransState(CooperationUtil.CONFIRM_UPLOAD_STATE);
                            //做本地哈希值检验
                            Transaction localReceivedTx = localConsortiumBlock.getInputTxs().get(receivedTx.getTransId());
                            localReceivedTx.setTransState(CooperationUtil.CONFIRM_UPLOAD_STATE);
                            localReceivedTx.setPreHash(receivedTx.getPreHash());
                            if (receivedTx.getHash().equals(CryptoUtil.calcTxHash(localReceivedTx))) {
                                //说明前序节点的任务没有问题
                                correspondingOutputTx.setPreHash(receivedTx.getHash());
                                correspondingOutputTx.setHash(CryptoUtil.calcTxHash(correspondingOutputTx));

                                //如果是最后的任务
                                if (correspondingOutputTx.getReceiverId().equals(CooperationUtil.FINISH_USER_ID)) {
                                    String receiverAddress = NetworkUtil.getAddressById(receivedTx.getSenderId());
                                    consortiumBPClient.init(receiverAddress, uploadData(receivedTx, CryptoUtil.encryptTx(correspondingOutputTx)));
                                } else {
                                    //否则往后传递确认任务
                                    String receiverAddress = NetworkUtil.getAddressById(correspondingOutputTx.getReceiverId());
                                    consortiumBPClient.init(receiverAddress, confirmForUpload(correspondingOutputTx));
                                }
                            }
                        }
                        break;
                    default:
                        System.out.println("当前交易的状态错误！");
                        break;
                }
            }
        }
        else {
            System.out.println("交易不存在");
        }
    }

    @Override
    public void handleUploadData(String bpData) {
        //反序列化得到其它节点传来的协作流程
        //receivedInfo[0]是当前节点传出的后续任务，receivedInfo[1]是当前后续任务的其他后续任务
        String[] receivedInfo = bpData.split("$$");
        Transaction receivedOutputTx = JSON.parseObject(receivedInfo[0], Transaction.class);

        //获取字符串分割后续的全部字符，即其他的后续合作事件
        String otherEvents = receivedInfo[1];

        ConsortiumBlock localConsortiumBlock = localCooperation.getLocalConsortiumChain().get(receivedOutputTx.getBpId());

        //前向加密保存数据，最后所有的事情都保存在第一个用户节点中
        //先验证后续节点的正确性
        if (localConsortiumBlock.getOutputTxs().containsKey(receivedOutputTx.getTransId())) {
            Transaction localOutputTx = localConsortiumBlock.getOutputTxs().get(receivedOutputTx.getTransId());
            //说明后继节点的任务没有问题
            if (receivedOutputTx.getHash().equals(CryptoUtil.calcTxHash(localOutputTx))) {
                //本地的后续节点对应的前置节点
                Integer correspondingInputTxId = localConsortiumBlock.getOutputs2Inputs().get(receivedOutputTx.getTransId());
                //将数据保存在本地，然后等待其他的后续事件完成的通知
                List<String> outputTxsData;
                if (localConsortiumBlock.getUploadData().containsKey(correspondingInputTxId)) {
                    outputTxsData = localConsortiumBlock.getUploadData().get(correspondingInputTxId);
                } else {
                    outputTxsData = new ArrayList<>();
                }
                outputTxsData.add(CryptoUtil.encryptTx(receivedOutputTx) + otherEvents);
                localConsortiumBlock.getUploadData().put(correspondingInputTxId, outputTxsData);

                //如果当前前置事件的所有后续事件都完成了，就继续往前传递数据
                if (localConsortiumBlock.isOver(correspondingInputTxId)) {
                    //如果当前节点是第一个用户，则将数据上传到公有链
                    if (correspondingInputTxId.equals(CooperationUtil.FIRST_TX)) {
                        uploadPhase(localConsortiumBlock);
                    }
                    else {
                        Transaction correspondingInputTx = localConsortiumBlock.getInputTxs().get(correspondingInputTxId);
                        StringBuilder localEncryptTxs = new StringBuilder();
                        for (String outputTx : localConsortiumBlock.getUploadData().get(correspondingInputTxId)) {
                            localEncryptTxs.append("@@").append(outputTx);
                        }
                        //如果不是第一个用户，继续往前传递数据
                        String receiverAddress = NetworkUtil.getAddressById(correspondingInputTx.getSenderId());
                        consortiumBPClient.init(receiverAddress, uploadData(correspondingInputTx, localEncryptTxs.toString()));
                    }
                }
            }
        }
    }

    @Override
    public String applyDeny() {
        return JSON.toJSONString(new NetworkMsg(BlockchainUtil.APPLY_DENY));
    }
}