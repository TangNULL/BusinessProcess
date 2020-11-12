package com.example.demo.serviceImpl;

import com.example.demo.entity.Transaction;
import com.example.demo.service.BusinessService;

import java.util.List;

public class BusinessServiceImpl implements BusinessService {
    @Override
    public void cooperate(String identity, int bpId, String BPDescription, List<Transaction> transactions) {

    }

    @Override
    public void createBusinessProcess(String identity, String BPDescription, List<Transaction> transactions) {

    }

    @Override
    public void processCooperationRequest(int contractId, boolean cooperationResponse) {

    }

    @Override
    public void confirmBusinessProcessCompletion(int contractId) {

    }
}
