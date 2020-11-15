package com.example.demo.controller;

import com.example.demo.entity.BusinessProcess;
import com.example.demo.entity.IResponse;
import com.example.demo.entity.LatestTransaction;
import com.example.demo.entity.Transaction;
import com.example.demo.service.BPManageService;
import com.example.demo.utils.ConvertObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PcController {
    @Autowired
    BPManageService bpManageService;

    @RequestMapping("/getLatestBusinessProcesses")
    public IResponse getLatestBusinessProcesses() {
        List<LatestTransaction> latestTransactionList;
        IResponse response;
        BusinessProcess bp = bpManageService.getLatestBusinessProcess();
        if (bp != null) {
            List<Transaction> transactionList = bpManageService.getTransactionsByBpId(bp.getBpId());
            latestTransactionList = ConvertObjectUtil.convertTransactionToLatestTransaction(bp.getBpId(), transactionList);
            response = new IResponse(0, latestTransactionList);
        } else
            response = new IResponse(0, "没有查询到结果");
        return response;
    }

    @RequestMapping("/getBusinessProcessesByBlockId")
    public IResponse getBusinessProcessesByBlockId(@RequestParam int blockId) {
        List<BusinessProcess> businessProcesses;
        IResponse response;
        businessProcesses = bpManageService.getBusinessProcessesByBlockId(blockId);
        if (businessProcesses != null) {
            response = new IResponse(0, businessProcesses);
        } else
            response = new IResponse(0, "没有查询到结果");
        return response;
    }


}
