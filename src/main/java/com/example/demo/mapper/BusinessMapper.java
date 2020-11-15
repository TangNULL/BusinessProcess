package com.example.demo.mapper;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessMapper {
    //bpcontract创建一条新的合同，拿到contractId，businessprocess加一条流程和合同的关联，
    //transactionDescription循环增加交易，并在cooperation增加合同和交易的关联
    //如果是新建流程 bpDescription新增一条 拿到bpId
    void insertBPDescription(BusinessProcess businessProcess);

    Integer insertContract(BPContract bpContract);

    void insertBusinessProcess(int bpId, int contractId);

    void batchInsertTransactions(List<Transaction> transactions);

    Integer InsertTransaction(Transaction transaction);

    void batchInsertCooperation(int contractId, @Param("list2") List<Transaction> transactions);
}
