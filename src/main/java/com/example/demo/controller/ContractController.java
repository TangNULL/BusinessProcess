package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.BPManageService;
import com.example.demo.service.BusinessService;
import com.example.demo.service.UserManageService;
import com.example.demo.utils.ConvertObjectUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ContractController {
    @Autowired
    BPManageService bpManageService;

    @Autowired
    BusinessService businessService;

    @Autowired
    UserManageService userManageService;

    @RequestMapping("/cooperate")
    public IResponse creatCooperate(@RequestParam Integer senderId, @RequestParam Integer receiverId, @RequestParam Integer bpId, @RequestParam String bpDescription, @RequestParam String transactionList) {
        IResponse response = null;
        //把交易的json解析出来
        //transactionList = "[{senderId:3,receiverId:4,tranDescription:交易内容}]";


        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Transaction>>() {
        }.getType();
        List<Transaction> list = gson.fromJson(transactionList, type);

        String s = "";
        int code = 0;
        /*List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(new Transaction(1, 2, "1 give 2 3yuan"));
        transactionList.add(new Transaction(2, 1, "2 give 1 2 bags"));*/
        if (bpId == null || bpId.equals("")) {
            bpId = null;
        }
        try {
            businessService.creatCooperate(senderId, receiverId, bpId, bpDescription, list);
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

    @RequestMapping("/getAllContractsByBPIdAndUserId")
    public IResponse getAllContractsByBPIdAndUserId(@RequestParam Integer bpId, @RequestParam Integer userId) {
        //用户点击"处理"获得某一流程下与其相关的所有的合同
        IResponse response;
        List<BPContract> bpContracts;
        bpContracts = bpManageService.getAllContractsByBPIdAndUserId(bpId, userId);
        if (bpContracts != null) {
            response = new IResponse(0, bpContracts);
        } else
            response = new IResponse(0, "查询结果为空");
        return response;
    }

    @RequestMapping("/getAllContractsPreByBPIdAndUserId")
    public IResponse getAllContractsPreByBPIdAndUserId(@RequestParam Integer bpId, @RequestParam Integer userId) {
        //用户点击"处理"获得某一流程下与其相关的所有的合同
        IResponse response;
        List<BPContract> bpContracts;
        bpContracts = bpManageService.getAllContractsByBPIdAndUserId(bpId, userId);
        List<ContractPre> contractPres = ConvertObjectUtil.convertContractToContractPre(bpContracts);
        for (ContractPre contractPre : contractPres) {
            String senderName = userManageService.getUserById(Integer.parseInt(contractPre.getBpSenderName())).getUsername();
            String receiverName = userManageService.getUserById(Integer.parseInt(contractPre.getBpReceiverName())).getUsername();
            contractPre.setBpSenderName(senderName);
            contractPre.setBpReceiverName(receiverName);
        }
        if (contractPres != null) {
            response = new IResponse(0, contractPres);
        } else
            response = new IResponse(0, "查询结果为空");
        return response;
    }

    @RequestMapping("/processCooperationRequest")
    public IResponse processCooperationRequest(@RequestParam Integer contractId, @RequestParam boolean processResult) {
        //bpReceiverId为userId 并且 isComplete为waiting
        IResponse response;
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

    @RequestMapping("/getBusinessProcessesPreByUserId")
    public IResponse getBusinessProcessesPreByUserId(@RequestParam Integer userId, @RequestParam String type) {
        IResponse response;
        List<BusinessProcessPre> businessProcessPres;
        businessProcessPres = bpManageService.getBusinessProcessPreByUserIdAndType(type, userId);
        if (businessProcessPres != null) {
            response = new IResponse(0, businessProcessPres);
        } else
            response = new IResponse(0, "查询结果为空");
        return response;
    }

    @RequestMapping("/processTxInCooperation")
    public IResponse processTxInCooperation(@RequestParam Integer userId, Integer contractId, @RequestParam Integer transId) {
        IResponse response;
        String s;
        int code = 0;
        try {
            businessService.processTxInCooperation(userId, transId);
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
    public IResponse completeCooperation(@RequestParam Integer userId, @RequestParam Integer bpId) {
        IResponse response;
        String s;
        int code = 0;
        try {
            String re = businessService.confirmBusinessProcessCompletion(userId, bpId);
            if (re.equals(""))
                s = "分支完成确认成功";
            else {
                s = re;
                code = 1;
            }
        } catch (Exception e) {
            s = "分支完成确认失败";
            code = 1;
            e.printStackTrace();
        }
        response = new IResponse(code, s);
        return response;
    }


}
