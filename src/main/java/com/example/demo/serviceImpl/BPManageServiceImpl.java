package com.example.demo.serviceImpl;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BPMapper;
import com.example.demo.service.BPManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BPManageServiceImpl implements BPManageService {

    @Autowired
    BPMapper bpMapper;

    @Override
    public BusinessProcess getLatestBusinessProcess() {
        return bpMapper.findLatestBP();
    }

    @Override
    public List<BusinessProcess> getBusinessProcessesByBlockId(int blockId) {
        return bpMapper.findBPsByBlockId(blockId);
    }

    @Override
    public List<BusinessProcess> getBusinessProcessByUserIdAndType(String type, Integer userId) {
        //首先找到业务流程的userList包含userId的BP
        // all:至少有一个合同是userId接受了的 或者 存在一个合同是userId发起的     getContractByBPIdAndUserId"处理"业务流程的页面！！！！！
        // waiting:all基础上，如果我是接收合同的人那么我应当接受了这个合同并且双方没有都确认完成，如果我是发起合同的人那么这个合同应当没被拒绝且双方没有都确认完成。
        // unclosed:all基础上，存在一个合同 接收者未处理合作请求 或者（被对应receiver接受，并且至少一方没确认合同完成）
        List<BusinessProcess> result = new ArrayList<>();
        List<BusinessProcess> bps = new ArrayList<>();
        List<Integer> bpIds = bpMapper.findAllBPIdsByUserId(userId);
        for (Integer i : bpIds) {
            BusinessProcess bp = bpMapper.findBPByBPId(i);
            for (BPContract bpContract : bp.getBpContractList()) {
                System.out.println(bpContract.getBpReceiverId());
                System.out.println(bpContract.getReceiverAccepted() == null);
                System.out.println(bpContract.getBpSenderId());
                boolean a = (bpContract.getReceiverAccepted() == null ? false : bpContract.getReceiverAccepted());
                if ((bpContract.getBpReceiverId() == userId && a) || bpContract.getBpSenderId() == userId) {
                    bps.add(bp);
                    break;
                }
            }
        }
        if (type.equals("all")) {
            result.addAll(bps);
        }
        if (type.equals("waiting")) {
            for (BusinessProcess bp : bps) {
                for (BPContract bpContract : bp.getBpContractList()) {
                    boolean c = bpContract.getReceiverAccepted() == null ? false : bpContract.getReceiverAccepted();
                    boolean d = bpContract.getReceiverAccepted() == null ? true : bpContract.getReceiverAccepted();
                    if ((bpContract.getBpReceiverId() == userId && c && (bpContract.getSenderAck() != true || bpContract.getReceiverAck() != true)) || (bpContract.getBpSenderId() == userId && d && (bpContract.getSenderAck() != true || bpContract.getReceiverAck() != true))) {
                        result.add(bp);
                        break;
                    }
                }
            }
        }
        if (type.equals("unclosed")) {
            for (BusinessProcess bp : bps) {
                for (BPContract bpContract : bp.getBpContractList()) {
                    boolean b = bpContract.getReceiverAccepted() == null ? true : bpContract.getReceiverAccepted();
                    if (b && (bpContract.getSenderAck() != true || bpContract.getReceiverAck() != true)) {
                        result.add(bp);
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<BPContract> getWaitingContract(int userId) {
        return bpMapper.findWaitingBPContractsByUserId(userId);
    }

    @Override
    public List<BPContract> getContractsByBPIdAndUserId(int bpId, int userId) {
        BusinessProcess bp = bpMapper.findBPByBPId(bpId);
        List<BPContract> result = new ArrayList<>();
        for (BPContract bpContract : bp.getBpContractList()) {
            boolean c = bpContract.getReceiverAccepted() == null ? false : bpContract.getReceiverAccepted();
            boolean d = bpContract.getReceiverAccepted() == null ? true : bpContract.getReceiverAccepted();
            if ((bpContract.getBpReceiverId() == userId && c && (bpContract.getSenderAck() != true || bpContract.getReceiverAck() != true)) || (bpContract.getBpSenderId() == userId && d && (bpContract.getSenderAck() != true || bpContract.getReceiverAck() != true))) {
                result.add(bpContract);
            }
        }
        return result;
    }

    @Override
    public List<BPContract> getAllContractsByBPId(int bpId) {
        return bpMapper.findBPContractsByBPId(bpId);
    }

    @Override
    public List<Transaction> getTransactionsByBpId(int bpId) {
        List<Transaction> transactionList = new ArrayList<>();
        BusinessProcess bp = bpMapper.findBPByBPId(bpId);
        if (bp != null) {
            for (BPContract contract : bp.getBpContractList()) {
                for (Transaction t : contract.getTransactionList()) {
                    transactionList.add(new Transaction(t));
                }
            }
        }
        return transactionList;
    }


}
