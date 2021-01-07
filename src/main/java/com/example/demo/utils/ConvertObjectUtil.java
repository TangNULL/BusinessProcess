package com.example.demo.utils;

import com.example.demo.entity.*;
import com.example.demo.entity.LatestTransaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConvertObjectUtil {

    public static List<LatestTransaction> convertTransactionToLatestTransaction(int bpId, List<Transaction> transactions) {
        List<LatestTransaction> latestTransactionList = new ArrayList<LatestTransaction>();
        for (Transaction t : transactions) {
            if (t.getCompleteTime() == null) {
                continue;
            }
            long tranTime = t.getCompleteTime().getTime() - t.getCreateTime().getTime();
            System.out.println(tranTime);
            String day = (tranTime / (24 * 60 * 60 * 1000)) == 0 ? "" : (tranTime / (24 * 60 * 60 * 1000)) + "天";
            String hour = ((tranTime / (60 * 60 * 1000)) % 24) == 0 ? "" : ((tranTime / (60 * 60 * 1000)) % 24) + "小时";
            String min = ((tranTime / (60 * 1000)) % 60) == 0 ? "" : ((tranTime / (60 * 1000)) % 60) + "分钟";
            String s = ((tranTime / 1000) % 60) == 0 ? "" : ((tranTime / 1000) % 60) + "秒";
            //String ms = (tranTime % 1000) == 0 ? "" : (tranTime % 1000) + "毫秒";
            String time = day + hour + min + s;
            LatestTransaction latestTransaction = new LatestTransaction(bpId, t.getTranDescription(), t.getHash(), t.getSenderId(), t.getReceiverId(), time);
            latestTransactionList.add(latestTransaction);
        }
        return latestTransactionList;
    }

    public static List<BusinessProcessPre> convertBPToBPPre(Integer userId, List<BusinessProcess> businessProcesses) {
        List<BusinessProcessPre> businessProcessPres = new ArrayList<>();
        for (BusinessProcess bp : businessProcesses) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            List<String> list = gson.fromJson(bp.getAckUsers(), type);
            List<Integer> userIdlist = new ArrayList<>();
            for (String s : list) {
                userIdlist.add(Integer.parseInt(s));
            }
            String state = "";
            if (!userIdlist.contains(userId)) {
                state = "waiting";
            }
            String userNames = "";
            for (User u : bp.getUserList()) {
                userNames = userNames + u.getUsername() + " ";

            }
            BusinessProcessPre businessProcessPre = new BusinessProcessPre(bp.getBpId(), userNames, bp.getCreateTime(), bp.getCompleteTime(), state);
            businessProcessPres.add(businessProcessPre);
        }
        return businessProcessPres;
    }


    /*public static List<ContractPre> convertContractToContractPre(List<BPContract> bpContractList) {
        List<ContractPre> contractPres = new ArrayList<>();
        for (BPContract c : bpContractList) {
            List<String> txJsons = new ArrayList<>();
            Gson gson = new Gson();
            for (Transaction tx : c.getTransactionList()) {
                txJsons.add(gson.toJson(tx));
            }
            ContractPre contractPre = new ContractPre(c.getContractId(), c.getBpSenderId() + "", c.getBpReceiverId() + "", c.getBpDescription(), txJsons, c.getReceiverAccepted());
            contractPres.add(contractPre);
        }
        return contractPres;
    }*/


}
