package com.example.demo.service;

import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.BusinessProcessPre;
import com.example.demo.entity.Transaction;

import java.util.List;

public interface BPManageService {

    BusinessProcess getLatestBusinessProcess();  //获得最新完成的业务流程

    List<BusinessProcess> getBusinessProcessesByBlockId(int blockId);   //公有链监管部分得到某一区块中的所有业务流程

    //首先找到业务流程的userList包含userId的BP
    // all:至少有一个合同是userId接受了的 或者 存在一个合同是userId发起
    // waiting:all基础上，只要确认流程完成的名单getAckUsers里没有我就可以            （弃用->如果我是接收合同的人那么我应当接受了这个合同并且存在一个tx双方没有都确认完成，如果我是发起合同的人那么这个合同应当没被拒绝且存在一个tx双方没有都确认完成。
    // unclosed:all基础上，bp的getAckUsers的size<流程参与人数        （弃用->存在一个合同 接收者未处理合作请求 或者（被对应receiver接受，并且至少一方没确认合同完成）
    // closed: all基础上，bp的getAckUsers的size=流程参与人数
    List<BusinessProcess> getBusinessProcessByUserIdAndType(String type, Integer userId);


    List<BusinessProcessPre> getBusinessProcessPreByUserIdAndType(String type, Integer userId);


    //List<BPContract> getWaitingContract(int userId);  //获得待处理的合作请求

    //List<BPContract> getContractsByBPIdAndUserId(int bpId, int userId);  //获得某一流程下与某用户相关的未结束的合同

    //List<BPContract> getAllContractsByBPIdAndUserId(int bpId, int userId);  //获得某一流程下与某用户相关的所有的合同


    //List<BPContract> getAllContractsByBPId(int bpId);  //处理业务流程时，需要根据流程id获得与登录用户相关的所有合约

    List<Transaction> getTransactionsByBpId(int bpId);   //获得业务流程包含的所有transaction

    List<Transaction> getAllTransactionsByBpIdAndUserId(int bpId, int userId);

    List<Transaction> getWaitingTransactionsByBpIdAndUserId(int bpId, int userId);

}
