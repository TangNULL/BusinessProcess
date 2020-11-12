package com.example.demo.service;

import com.example.demo.entity.Transaction;

import java.util.List;

public interface BusinessService {  //核心业务流程
    void cooperate(String identity, int bpId, String BPDescription, List<Transaction> transactions);  //发起和某个用户在某个流程上的合作

    //发起新的业务流程，合作对象可以是自己，transactions列表需要设置sender，receiver，TranDescription
    void createBusinessProcess(String identity, String BPDescription, List<Transaction> transactions);

    void processCooperationRequest(int contractId, boolean cooperationResponse);  //处理其他用户发来的合作请求，同意or拒绝

    void confirmBusinessProcessCompletion(int contractId);  //确认业务流程某一分支完成
}
