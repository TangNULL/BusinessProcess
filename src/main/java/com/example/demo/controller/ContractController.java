package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.BPManageService;
import com.example.demo.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ContractController {
    @Autowired
    BPManageService bpManageService;

    @Autowired
    BusinessService businessService;

    @RequestMapping("/cooperate")
    public IResponse creatCooperate(@RequestParam Integer senderId, @RequestParam Integer receiverId, @RequestParam Integer bpId, @RequestParam String bpDescription, @RequestParam String transJson) {
        IResponse response = null;
        //把交易的json解析出来
        String s = "";
        int code = 0;
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(new Transaction(1, 2, "1 give 2 3yuan"));
        transactionList.add(new Transaction(2, 1, "2 give 1 2 bags"));
        if (bpId == null || bpId.equals("")) {
            bpId = null;
        }
        try {
            businessService.creatCooperate(senderId, receiverId, bpId, bpDescription, transactionList);
            s = "发起合作成功";
        } catch (Exception e) {
            s = "发起合作失败";
            code = 1;
            e.printStackTrace();
        }
        response = new IResponse(code, s);
        return response;
    }

    @RequestMapping("/getWaitingCooperationRequest")
    public IResponse getWaitingCooperationRequest(@RequestParam Integer userId) {
        //bpReceiverId为userId 并且 isComplete为waiting
        IResponse response;
        List<BPContract> bpContracts;
        bpContracts = bpManageService.getWaitingContract(userId);
        if (bpContracts != null) {
            response = new IResponse(0, bpContracts);
        } else
            response = new IResponse(0, "查询结果为空");
        return response;
    }

    @RequestMapping("/getContractsByBPIdAndUserId")
    public IResponse getContractsByBPIdAndUserId(@RequestParam Integer bpId, @RequestParam Integer userId) {
        //用户点击"处理"获得某一流程下与其相关的未结束的合同
        IResponse response;
        List<BPContract> bpContracts;
        bpContracts = bpManageService.getContractsByBPIdAndUserId(bpId, userId);
        if (bpContracts != null) {
            response = new IResponse(0, bpContracts);
        } else
            response = new IResponse(0, "查询结果为空");
        return response;
    }

    @RequestMapping("/processCooperationRequest")
    public IResponse processCooperationRequest(@RequestParam Integer contractId, @RequestParam boolean processResult) {
        //bpReceiverId为userId 并且 isComplete为waiting
        IResponse response;
        List<BPContract> bpContracts;
        String s;
        int code = 0;
        try {
            businessService.processCooperationRequest(contractId, processResult);
            s = "处理合作请求成功";
        } catch (Exception e) {
            s = "处理合作请求失败";
            code = 1;
            e.printStackTrace();
        }
        response = new IResponse(code, s);
        return response;
    }

    @RequestMapping("/getBusinessProcessesByUserId")
    public IResponse getBusinessProcessesByUserId(@RequestParam Integer userId, @RequestParam String type) {
        IResponse response;
        List<BusinessProcess> businessProcesses;
        businessProcesses = bpManageService.getBusinessProcessByUserIdAndType(type, userId);
        if (businessProcesses != null) {
            response = new IResponse(0, businessProcesses);
        } else
            response = new IResponse(0, "查询结果为空");
        return response;
    }

    @RequestMapping("/processTxInCooperation")
    public IResponse processTxInCooperation(@RequestParam String whichPart, @RequestParam Integer contractId, @RequestParam Integer transId) {
        IResponse response;
        String s;
        int code = 0;
        try {
            businessService.processTxInCooperation(whichPart, transId);
            s = "交易确认成功";
        } catch (Exception e) {
            s = "交易确认失败";
            code = 1;
            e.printStackTrace();
        }
        response = new IResponse(code, s);
        return response;
    }

    @RequestMapping("/completeCooperation")
    public IResponse completeCooperation(@RequestParam String whichPart, @RequestParam Integer contractId) {
        IResponse response;
        String s;
        int code = 0;
        try {
            businessService.confirmBusinessProcessCompletion(whichPart, contractId);
            s = "合同完成确认成功";
        } catch (Exception e) {
            s = "合同完成确认失败";
            code = 1;
            e.printStackTrace();
        }
        response = new IResponse(code, s);
        return response;
    }


}