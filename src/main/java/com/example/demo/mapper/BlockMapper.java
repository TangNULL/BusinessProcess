package com.example.demo.mapper;

import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockMapper {
    BusinessProcess findBPByBPId(int bpId);

    BusinessProcess findLatestBP();

    List<BusinessProcess> findBPsByBlockId(int blockId);

    List<BusinessProcess> findAllBPsByUserId(int userId); //注意加distinct

    //List<BusinessProcess> findWaitingBPsByUserId(int userId);//注意加distinct

    //List<BusinessProcess> findUnclosedBPsByUserId(int userId);//注意加distinct

    //List<BusinessProcess> findClosedBPsByUserId(int userId);//注意加distinct

    Transaction findTransactionInInputByTranId(int tranId);

    Transaction findTransactionInOutputByTranId(int tranId);

    List<Transaction> findAllInputTxsByBPId(int businessProcessId);

    List<Transaction> findAllOutputTxsByBPId(int businessProcessId);

    //List<Transaction> findAllInputTxsByUserIdAndBpId(int userId, int bpId);

    //List<Transaction> findAllOutputTxsByUserIdAndBpId(int userId, int bpId);

    List<Transaction> findWaitingTxsByUserIdAndBpId(int userId, int bpId);

    //List<Transaction> findWaitingTxsByUserId(int userId);

    //insert
    //插入一条新的流程获得bpId，仔插入一条交易
    void insertBPDescription(BusinessProcess businessProcess);

    //void insertBP_Tx(int bpId, int transId); //流程新增一条交易

    Integer insertTransaction_input(Transaction transaction);

    Integer insertTransaction_output(Transaction transaction);

    void updateBP(BusinessProcess businessProcess);

    void updateTransaction_input(Transaction transaction);

    void updateTransaction_output(Transaction transaction);

    List<Transaction> findAllInputTxs();

    List<Transaction> findAllOutputTxs();
}
