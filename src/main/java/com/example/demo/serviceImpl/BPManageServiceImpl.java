package com.example.demo.serviceImpl;

import com.example.demo.entity.BPContract;
import com.example.demo.entity.BusinessProcess;
import com.example.demo.service.BPManageService;

import java.util.List;

public class BPManageServiceImpl implements BPManageService {
    @Override
    public List<BPContract> getLatestContract() {
        return null;
    }

    @Override
    public List<BusinessProcess> getBusinessProcessByBlockId() {
        return null;
    }

    @Override
    public List<BusinessProcess> getAllBusinessProcessByUserIdentity(String type, String identity) {
        return null;
    }

    @Override
    public List<BPContract> getWaitingContract() {
        return null;
    }

    @Override
    public List<BPContract> getAllContractsByBPId(int bpId) {
        return null;
    }
}
