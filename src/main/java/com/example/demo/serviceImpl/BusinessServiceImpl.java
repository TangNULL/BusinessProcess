package com.example.demo.serviceImpl;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BPMapper;
import com.example.demo.mapper.BusinessMapper;
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
        BPContract bpContract = new BPContract(senderId, receiverId, BPDescription);
        businessMapper.insertContract(bpContract);
        businessMapper.insertBusinessProcess(businessProcesssId, bpContract.getContractId());
        businessMapper.batchInsertTransactions(transactions);
        businessMapper.batchInsertCooperation(bpContract.getContractId(), transactions);
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
    public void confirmBusinessProcessCompletion(Integer userId, int bpId) {
        BusinessProcess businessProcess = bpMapper.findBPByBPId(bpId);
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
        }
        businessProcess.setAckUsers(gson.toJson(userIdlist));
        bpMapper.updateBP(businessProcess);
    }

    @Override
    public void processTxInCooperation(String whichPart, int transId) {
        Transaction t = bpMapper.findTransactionByTranId(transId);
        if (t.getReceiverAck() || t.getSenderAck()) {
            //加上这一个确认 这笔交易完成了
            Timestamp comTime = new Timestamp(System.currentTimeMillis());
            t.setCompleteTime(comTime);
            t.setHash();
        }
        if (whichPart.equals("sender")) {
            t.setSenderAck(true);
        } else
            t.setReceiverAck(true);
        bpMapper.updateTransaction(t);

    }
}
