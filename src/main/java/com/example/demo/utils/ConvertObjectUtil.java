package com.example.demo.utils;

import com.example.demo.entity.*;
import com.example.demo.entity.LatestTransaction;

import java.util.ArrayList;
import java.util.List;

public class ConvertObjectUtil {

    public static List<LatestTransaction> convertTransactionToLatestTransaction(int bpId, List<Transaction> transactions) {
        List<LatestTransaction> latestTransactionList = new ArrayList<LatestTransaction>();
        for (Transaction t : transactions) {
            long tranTime = t.getCompleteTime().getTime() - t.getCreateTime().getTime();
            System.out.println(tranTime);
            String day = (tranTime / (24 * 60 * 60 * 1000)) == 0 ? "" : (tranTime / (24 * 60 * 60 * 1000)) + "天";
            String hour = ((tranTime / (60 * 60 * 1000)) % 24) == 0 ? "" : ((tranTime / (60 * 60 * 1000)) % 24) + "小时";
            String min = ((tranTime / (60 * 1000)) % 60) == 0 ? "" : ((tranTime / (60 * 1000)) % 60) + "分钟";
            String s = ((tranTime / 1000) % 60) == 0 ? "" : ((tranTime / 1000) % 60) + "秒";
            //String ms = (tranTime % 1000) == 0 ? "" : (tranTime % 1000) + "毫秒";
            String time = day + hour + min + s;
            LatestTransaction latestTransaction = new LatestTransaction(bpId, t.getHash(), t.getSenderId(), t.getReceiverId(), time);
            latestTransactionList.add(latestTransaction);
        }
        return latestTransactionList;
    }


}
