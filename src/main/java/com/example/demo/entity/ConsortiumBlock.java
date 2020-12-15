package com.example.demo.entity;

import com.example.demo.utils.CooperationUtil;

import java.util.*;

public class ConsortiumBlock {
    private Map<Integer, Transaction> inputTxs;  //本地节点保存的接收到的协作请求，Map<txId, inputTx>
    private Map<Integer, Transaction> outputTxs;  //本地节点上保存的发出的协作请求，Map<txId, outputTx>
    private Map<Integer, Integer> inputs2Outputs;  //本地节点的协作对应关系，Map<outputTxId, inputTxId> 为了响应哪个tx的协作请求
    private Map<Integer, User> inputUsers;  //本地节点保存的需要和自己协作用户，Map<txId, inputUser>
    private Map<Integer, User> outputUsers;  //本地节点保存的需要的其他协作用户，Map<txId, outputUser>
    private String pkHash;  //用户私钥的哈希值
    private int bpId;  //标识唯一流程实例
    private int finishedTxCount;  //标记当前的用户后续任务有多少已经完成

    public ConsortiumBlock(int bpId) {
        this.inputTxs = new HashMap<>();
        this.outputTxs = new HashMap<>();
        this.inputs2Outputs = new HashMap<>();
        this.inputUsers = new HashMap<>();
        this.outputUsers = new HashMap<>();
        finishedTxCount = 0;
        this.bpId = bpId;
    }

    public Map<Integer, Transaction> getInputTxs() {
        return inputTxs;
    }

    public void setInputTxs(Map<Integer, Transaction> inputTxs) {
        this.inputTxs = inputTxs;
    }

    public Map<Integer, Transaction> getOutputTxs() {
        return outputTxs;
    }

    public void setOutputTxs(Map<Integer, Transaction> outputTxs) {
        this.outputTxs = outputTxs;
    }

    public Map<Integer, Integer> getInputs2Outputs() {
        return inputs2Outputs;
    }

    public void setInputs2Outputs(Map<Integer, Integer> inputs2Outputs) {
        this.inputs2Outputs = inputs2Outputs;
    }

    public Map<Integer, User> getInputUsers() {
        return inputUsers;
    }

    public void setInputUsers(Map<Integer, User> inputUsers) {
        this.inputUsers = inputUsers;
    }

    public Map<Integer, User> getOutputUsers() {
        return outputUsers;
    }

    public void setOutputUsers(Map<Integer, User> outputUsers) {
        this.outputUsers = outputUsers;
    }

    public String getPkHash() {
        return pkHash;
    }

    public void setPkHash(String pkHash) {
        this.pkHash = pkHash;
    }

    public int getBpId() {
        return bpId;
    }

    public void setBpId(int bpId) {
        this.bpId = bpId;
    }

    public int getFinishedTxCount() {
        return finishedTxCount;
    }

    public void setFinishedTxCount(int finishedTxCount) {
        this.finishedTxCount = finishedTxCount;
    }

    /**
     * 给本地用户添加合作用户
     * @param userType 输入用户还是输出用户
     * @param transId 合作的事件transId
     * @param newUser 用户信息
     * @return 是否添加成功
     */
    public boolean addUser(int userType, int transId, User newUser) {
        //User是需要合作的其他用户
        if (userType == CooperationUtil.OUTPUT_USER) {
            outputUsers.put(transId, newUser);
            return true;
        }
        //User是申请和我合作的其他用户
        else if (userType == CooperationUtil.INPUT_USER) {
            inputUsers.put(transId, newUser);
            return true;
        }
        return false;
    }

    /**
     * 利用合作描述判断合作的唯一性
     * @param txType 合作类型
     * @param transId 合作对象的transId
     * @param newTx 新的合作交易
     * @return 是否添加成功
     */
    public boolean addTx(int txType, int transId, Transaction newTx) {
        //先判断是否是同一个流程实例
        if (!newTx.getBpId().equals(this.bpId)) {
            return false;
        }

        //Tx是第一个业务流程合作tx
        if (txType == CooperationUtil.FIRST_TX) {
            outputTxs.put(newTx.getTransId(), newTx);
            inputs2Outputs.put(newTx.getTransId(), CooperationUtil.FIRST_TX);
            return true;
        }

        //Tx没有后续的合作
        if (txType == CooperationUtil.LAST_TX) {
            outputTxs.put(CooperationUtil.LAST_TX, newTx);
            inputs2Outputs.put(CooperationUtil.LAST_TX, transId);
        }

        //Tx是申请和我合作的tx
        if (txType == CooperationUtil.INPUT_TX) {
            if (inputTxs.isEmpty()) {
                inputTxs.put(newTx.getTransId(), newTx);
                return true;
            }
            if (!newTx.getBpId().equals(this.bpId)) {
                return false;
            }
            if (inputTxs.containsKey(newTx.getTransId())) {
                return false;
            }
            inputTxs.put(newTx.getTransId(), newTx);
            return true;
        }
        //Tx是我申请其他人的合作的tx
        else if (txType == CooperationUtil.OUTPUT_TX) {
            if (inputTxs.containsKey(txType)) {
                if (outputTxs.isEmpty()) {
                    outputTxs.put(newTx.getTransId(), newTx);
                    inputs2Outputs.put(newTx.getTransId(), transId);
                    return true;
                }
                if (outputTxs.containsKey(newTx.getTransId())) {
                    return false;
                }
                outputTxs.put(newTx.getTransId(), newTx);
                inputs2Outputs.put(newTx.getTransId(), transId);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加标记完成的合作
     * @param finishedTx 需要确认的完成的合作
     * @return 返回当前完成的合作的前置合作的transId
     */
    public Integer addFinishedOutputTxs(Transaction finishedTx) {
        if (outputTxs.containsKey(finishedTx.getTransId())) {
            finishedTxCount ++;
            return inputs2Outputs.get(finishedTx.getTransId());
        }
        else return CooperationUtil.ERROR_USER_ID;
    }


    /**
     *
     * @return 判断当前用户的全部合作是否已经完成
     */
    public boolean isFinished() {
        return outputTxs.size() == finishedTxCount;
    }
}
