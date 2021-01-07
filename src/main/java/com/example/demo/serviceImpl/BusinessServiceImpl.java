package com.example.demo.serviceImpl;

import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BlockMapper;
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
    BlockMapper blockMapper;

    @Autowired
    ConsortiumBlockchainService consortiumBlockchainService;

    @Override
    public void creatCooperate(Integer bpId, Transaction tx) {
        int businessProcesssId;
        BusinessProcess bp = new BusinessProcess();
        if (bpId == null) { //需要新建流程
            blockMapper.insertBPDescription(bp);
            businessProcesssId = bp.getBpId();
        } else {
            businessProcesssId = bpId;
        }
        tx.setBpId(businessProcesssId);
        tx.setSenderAck(true);
        //协作业务流程发起者申请联盟链
        if (consortiumBlockchainService.downloadPhase(tx)) {
            blockMapper.insertTransaction_output(tx);
            blockMapper.insertTransaction_input(tx);
        } else {
            System.out.println("该用户不存在，请先实名注册！");
        }
    }

    /*@Override
    public void createBusinessProcess(String id, String BPDescription, List<Transaction> transactions) {

    }*/

    //"默认接收合作"
    /*@Override
    public void processCooperationRequest(int contractId, boolean cooperationResponse) {
        BPContract bpContract = new BPContract();
        bpContract.setContractId(contractId);
        bpContract.setReceiverAccepted(cooperationResponse);
        bpMapper.updateBPContract(bpContract);
    }*/

    @Override
    public String confirmBusinessProcessCompletion(Integer userId, int bpId) {
        List<Transaction> txs = blockMapper.findWaitingTxsByUserIdAndBpId(userId, bpId);
        if (txs != null && txs.size() > 0) {
            return "还有交易未完成";
        }
        BusinessProcess businessProcess = blockMapper.findBPByBPId(bpId);
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
            //TODO 合作结束上传协作数据

//                if(consortiumBlockchainService.generatePhase()){
//
//                }


        }
        businessProcess.setAckUsers(gson.toJson(userIdlist));
        blockMapper.updateBP(businessProcess);
        return "";

    }

    @Override
    public void processTxInCooperation(Integer userId, int transId) {
        //只处理自己接受的任务，意味着这笔tx也完成了，不需要再判断sender了
        Transaction t = blockMapper.findTransactionByTranId(transId);
        t.setReceiverAck(true);
        Timestamp comTime = new Timestamp(System.currentTimeMillis());
        t.setCompleteTime(comTime);
        t.setHash();
        blockMapper.updateTransaction_input(t);
        blockMapper.updateTransaction_output(t);
    }
}
