package com.example.demo.serviceImpl;

import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.BusinessProcessPre;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BlockMapper;
import com.example.demo.service.BPManageService;
import com.example.demo.utils.ConvertObjectUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class BPManageServiceImpl implements BPManageService {

    @Autowired
    BlockMapper blockMapper;

    @Override
    public BusinessProcess getLatestBusinessProcess() {
        return blockMapper.findLatestBP();
    }

    @Override
    public List<BusinessProcess> getBusinessProcessesByBlockId(int blockId) {
        return blockMapper.findBPsByBlockId(blockId);
    }

    @Override
    public List<BusinessProcess> getBusinessProcessByUserIdAndType(String type, Integer userId) {
        //首先找到业务流程的userList包含userId的BP
        // all:至少有一个合同是userId接受了的 或者 存在一个合同是userId发起
        // waiting:all基础上，只要确认流程完成的名单getAckUsers里没有我就可以            （弃用->如果我是接收合同的人那么我应当接受了这个合同并且存在一个tx双方没有都确认完成，如果我是发起合同的人那么这个合同应当没被拒绝且存在一个tx双方没有都确认完成。
        // unclosed:all基础上，bp的getAckUsers的size<流程参与人数        （弃用->存在一个合同 接收者未处理合作请求 或者（被对应receiver接受，并且至少一方没确认合同完成）
        // closed: all基础上，bp的getAckUsers的size=流程参与人数
        List<BusinessProcess> result = new ArrayList<>();
        List<BusinessProcess> bps = blockMapper.findAllBPsByUserId(userId);
        if (type.equals("all")) {
            return bps;
        }
        if (type.equals("waiting")) {
            for (BusinessProcess bp : bps) {
                Gson gson = new Gson();
                Type type1 = new TypeToken<ArrayList<String>>() {
                }.getType();
                List<String> list = gson.fromJson(bp.getAckUsers(), type1);
                List<Integer> userIdlist = new ArrayList<>();
                for (String s : list) {
                    userIdlist.add(Integer.parseInt(s));
                }
                if (!userIdlist.contains(userId)) {
                    result.add(bp);
                }
            }
        }
        if (type.equals("unclosed")) {
            for (BusinessProcess bp : bps) {
                if (bp.getCompleteTime() == null) {
                    result.add(bp);
                }
            }
        }
        if (type.equals("closed")) {
            for (BusinessProcess bp : bps) {
                if (bp.getCompleteTime() != null) {
                    result.add(bp);
                }
            }
        }
        return result;
    }

    @Override
    public List<BusinessProcessPre> getBusinessProcessPreByUserIdAndType(String type, Integer userId) {
        List<BusinessProcess> businessProcesses = getBusinessProcessByUserIdAndType(type, userId);
        List<BusinessProcessPre> businessProcessPres = ConvertObjectUtil.convertBPToBPPre(userId, businessProcesses);
        return businessProcessPres;
    }

    /*@Override
    public List<BPContract> getWaitingContract(int userId) {
        return bpMapper.findWaitingBPContractsByUserId(userId);
    }

    @Override
    public List<BPContract> getContractsByBPIdAndUserId(int bpId, int userId) {
        BusinessProcess bp = bpMapper.findBPByBPId(bpId);
        List<BPContract> result = new ArrayList<>();
        for (BPContract bpContract : bp.getBpContractList()) {
            boolean existTxNotBothAck = false;
            for (Transaction t : bpContract.getTransactionList()) {
                if (!t.getSenderAck() || !t.getReceiverAck()) {
                    existTxNotBothAck = true;
                    break;
                }
            }
            boolean c = bpContract.getReceiverAccepted() == null ? false : bpContract.getReceiverAccepted();
            //boolean d = bpContract.getReceiverAccepted() == null ? true : bpContract.getReceiverAccepted();
            if ((bpContract.getBpReceiverId() == userId && c && existTxNotBothAck) || (bpContract.getBpSenderId() == userId && existTxNotBothAck)) {
                result.add(bpContract);
            }
        }
        return result;
    }

    @Override
    public List<BPContract> getAllContractsByBPIdAndUserId(int bpId, int userId) {
        BusinessProcess bp = bpMapper.findBPByBPId(bpId);
        List<BPContract> result = new ArrayList<>();
        for (BPContract bpContract : bp.getBpContractList()) {
            if (bpContract.getBpReceiverId() == userId || bpContract.getBpSenderId() == userId) {
                result.add(bpContract);
            }
        }
        return result;
    }

    @Override
    public List<BPContract> getAllContractsByBPId(int bpId) {
        return bpMapper.findBPContractsByBPId(bpId);
    }
*/
    @Override
    public List<Transaction> getTransactionsByBpId(int bpId) {
        //TODO 这里的bp只包含了本地用户相关的tx
        List<Transaction> list = new ArrayList<>();
        list.addAll(blockMapper.findAllInputTxsByBPId(bpId));
        list.addAll(blockMapper.findAllOutputTxsByBPId(bpId));
        return list;
    }

    @Override
    public List<Transaction> getAllTransactionsByBpIdAndUserId(int bpId, int userId) {
        //获得与用户相关的所有tx
        List<Transaction> list = new ArrayList<>();
        list.addAll(blockMapper.findAllInputTxsByBPId(bpId));
        list.addAll(blockMapper.findAllOutputTxsByBPId(bpId));
        return list;
    }

    @Override
    public List<Transaction> getWaitingTransactionsByBpIdAndUserId(int bpId, int userId) {
        return blockMapper.findWaitingTxsByUserIdAndBpId(userId, bpId);
    }


}
