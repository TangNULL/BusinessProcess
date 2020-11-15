package com.example.demo.mapper;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BPMapper {
    BusinessProcess findBPByBPId(int bpId);

    BusinessProcess findLatestBP();

    List<BPContract> findBPContractsByBPId(int businessProcessId);

    BPContract findBPContractBycontractId(int contractId);

    List<BPContract> findWaitingBPContractsByUserId(int userId);

    List<BusinessProcess> findBPsByBlockId(int blockId);

    void updateBPContract(BPContract bpContract);

    void updateTransaction(Transaction transaction);

    List<Integer> findAllBPIdsByUserId(int userId);   //有重复！！！！！！

    Transaction findTransactionByTranId(int tranId);

}
