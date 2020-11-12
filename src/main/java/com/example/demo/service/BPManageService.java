package com.example.demo.service;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;

import java.util.List;

public interface BPManageService {
    List<BPContract> getLatestContract();  //获得最新完成的合作

    List<BusinessProcess> getBusinessProcessByBlockId();   //公有链监管部分得到某一区块中的业务流程

    //获取登录用户相关的协作业务流程
    //type的取值=>  all:所有的   doing：进行中的    waiting：待办的     done：已完成的
    List<BusinessProcess> getAllBusinessProcessByUserIdentity(String type, String identity);

    List<BPContract> getWaitingContract();  //获得待处理的合作请求

    List<BPContract> getAllContractsByBPId(int bpId);  //处理业务流程时，需要根据流程id获得与登录用户相关的所有合约
}
