package com.example.demo.serviceImpl;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BPMapper;
import com.example.demo.mapper.BusinessMapper;
import com.example.demo.service.ConsortiumBlockchainService;
import com.example.demo.service.BusinessService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    BusinessMapper businessMapper;

    @Autowired
    BPMapper bpMapper;

    @Autowired
    ConsortiumBlockchainService consortiumBlockchainService;

    @Override
    public void creatCooperate(Integer senderId, Integer receiverId, Integer bpId, String BPDescription, List<Transaction> transactions) {
        int businessProcesssId;
        BusinessProcess bp = new BusinessProcess();
        if (bpId == null) { //需要新建流程
            businessMapper.insertBPDescription(bp);
            businessProcesssId = bp.getBpId();
        } else {
            businessProcesssId = bpId;
        }

        //协作业务流程发起者申请联盟链
        if(consortiumBlockchainService.downloadPhase(businessProcesssId, senderId, receiverId, BPDescription)){
            BPContract bpContract = new BPContract(senderId, receiverId, BPDescription);
            businessMapper.insertContract(bpContract);
            businessMapper.insertBusinessProcess(businessProcesssId, bpContract.getContractId());
            businessMapper.batchInsertTransactions(transactions);
            businessMapper.batchInsertCooperation(bpContract.getContractId(), transactions);
        } else {
            System.out.println("该用户不存在，请先实名注册！");
        }
    }

    /*@Override
    public void createBusinessProcess(String id, String BPDescription, List<Transaction> transactions) {

    }*/

    @Override
    public void processCooperationRequest(int contractId, boolean cooperationResponse) {
        BPContract bpContract = new BPContract();
        bpContract.setContractId(contractId);
        bpContract.setReceiverAccepted(cooperationResponse);
        bpMapper.updateBPContract(bpContract);
    }

    @Override
    public String confirmBusinessProcessCompletion(Integer userId, int bpId) {
        BusinessProcess businessProcess = bpMapper.findBPByBPId(bpId);
        String result = "";
        boolean existTxNotComplete = false;
        for (BPContract contract : businessProcess.getBpContractList()) {
            if (contract.getBpSenderId() == userId || contract.getBpReceiverId() == userId) {
                if (contract.getReceiverAccepted() == null) {
                    result = "有一个关于您的合同还没被userId:" + contract.getBpReceiverId() + "处理";
                    break;
                } else if (contract.getReceiverAccepted()) {
                    for (Transaction t : contract.getTransactionList()) {
                        if (t.getSenderId() == userId || t.getReceiverId() == userId) {
                            if (!t.getReceiverAck() || !t.getSenderAck()) {
                                existTxNotComplete = true;
                                break;
                            }
                        }
                    }
                    if (existTxNotComplete) {
                        result = "还有交易未完成";
                        break;
                    }
                }

            }

        }
        if (!result.equals("")) {
            return result;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            List<String> list = gson.fromJson(businessProcess.getAckUsers(), type);
            List<Integer> userIdlist = new ArrayList<>();
            for (String s : list) {
                userIdlist.add(Integer.parseInt(s));
            }
            userIdlist.add(userId);
            if (userIdlist.size() == businessProcess.getUserList().size()) {
                Timestamp comTime = new Timestamp(System.currentTimeMillis());
                businessProcess.setCompleteTime(comTime);
                //TODO
                if(consortiumBlockchainService.uploadPhase()){
                    
                }


            }
            businessProcess.setAckUsers(gson.toJson(userIdlist));
            bpMapper.updateBP(businessProcess);
            return result;
        }
    }

    @Override
    public void processTxInCooperation(Integer userId, int transId) {
        Transaction t = bpMapper.findTransactionByTranId(transId);
        if (t.getReceiverAck() || t.getSenderAck()) {
            //加上这一个确认 这笔交易完成了
            Timestamp comTime = new Timestamp(System.currentTimeMillis());
            t.setCompleteTime(comTime);
            t.setHash();
        }
        if (userId == t.getSenderId()) {
            t.setSenderAck(true);
        } else if (userId == t.getReceiverId())
            t.setReceiverAck(true);
        bpMapper.updateTransaction(t);

    }
}
