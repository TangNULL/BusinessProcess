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
    public void creatCooperate(Integer bpId, Transaction tx, Integer lastTxId) {
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
        tx.setTransId(lastTxId + 1);
        blockMapper.insertTransaction_output(tx);
        //协作业务流程发起者申请联盟链
        if (tx.getTransId().equals(1)) {
            try {
                if (consortiumBlockchainService.downloadPhase(tx)) {
                    System.out.println("发起合作成功！");
                }
            } catch (Exception e) {
                System.out.println("该用户不存在，请先实名注册！");
            }
        } else {
            //协作业务流程响应其他人的任务
            try {
                if (consortiumBlockchainService.generatePhase(tx)) {
                    System.out.println("发起合作成功！");
                }
            } catch (Exception e) {
                System.out.println("该用户不存在，请先实名注册！");
            }
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
        Transaction t = blockMapper.findTransactionInInputByTranId(transId);
        t.setReceiverAck(true);
        Timestamp comTime = new Timestamp(System.currentTimeMillis());
        t.setCompleteTime(comTime);
        t.setHash();
        blockMapper.updateTransaction_input(t);

        //通过看所有接收者是否都ack 判断流程是否结束
        int bpId = t.getBpId();
        BusinessProcess bp = blockMapper.findBPByBPId(bpId);
        boolean allOver = true;
        //TODO: 这里需要查所有的tx，而不是只有本地的
        for (Transaction transaction : bp.getTxList()) {
            if (!transaction.getReceiverAck()) {
                allOver = false;
                break;
            }
        }
        if (allOver) {
            Timestamp comTime2 = new Timestamp(System.currentTimeMillis());
            bp.setCompleteTime(comTime2);
            blockMapper.updateBP(bp);
        }
    }
}
