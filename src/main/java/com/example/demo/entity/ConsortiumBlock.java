package com.example.demo.entity;

import com.example.demo.utils.CooperationUtil;

import java.util.*;

public class ConsortiumBlock {
    private String pkHash;  //用户私钥的哈希值
    private int bpId;  //标识唯一流程实例
    private Map<Integer, Transaction> inputTxs;  //本地节点保存的接收到的协作请求，Map<txId, inputTx>
    private Map<Integer, Transaction> outputTxs;  //本地节点上保存的发出的协作请求，Map<txId, outputTx>
    private Map<Integer, List<Integer>> inputs2Outputs;  //本地节点的协作对应关系，Map<inputTxId, List<outputTxId>> 为了响应哪个tx的协作请求
    private Map<Integer, Integer> outputs2Inputs;  //本地节点的协作对应关系，Map<outputTxId, inputTxId> 为了响应哪个tx的协作请求
    private Map<Integer, User> inputUsers;  //本地节点保存的需要和自己协作用户，Map<txId, inputUser>
    private Map<Integer, User> outputUsers;  //本地节点保存的需要的其他协作用户，Map<txId, outputUser>
    private Map<Integer, Integer> finishedTxsCount;  //标记当前的任务对应的后续任务有多少已经完成，Map<inputTxId, finishedTxCount>
    private Map<Integer, List<Integer>> finishedTxs;  //当前任务哪些后续任务完成了，Map<inputTxId, List<outputTxId>>
    private Map<Integer, List<String>> uploadData;  //当前节点上需要上传的前置事件对应的后续事件的处理结果，Map<inputTxId, List<EncryptTx>>

    public ConsortiumBlock(int bpId) {
        this.bpId = bpId;
        this.inputTxs = new HashMap<>();
        this.outputTxs = new HashMap<>();
        this.inputs2Outputs = new HashMap<>();
        this.outputs2Inputs = new HashMap<>();
        this.inputUsers = new HashMap<>();
        this.outputUsers = new HashMap<>();
        this.finishedTxsCount = new HashMap<>();
        this.finishedTxs = new HashMap<>();
        this.uploadData = new HashMap<>();

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

    public Map<Integer, List<Integer>> getInputs2Outputs() {
        return inputs2Outputs;
    }

    public void setInputs2Outputs(Map<Integer, List<Integer>> inputs2Outputs) {
        this.inputs2Outputs = inputs2Outputs;
    }

    public Map<Integer, Integer> getOutputs2Inputs() {
        return outputs2Inputs;
    }

    public void setOutputs2Inputs(Map<Integer, Integer> outputs2Inputs) {
        this.outputs2Inputs = outputs2Inputs;
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

    public Map<Integer, Integer> getFinishedTxsCount() {
        return finishedTxsCount;
    }

    public void setFinishedTxsCount(Map<Integer, Integer> finishedTxsCount) {
        this.finishedTxsCount = finishedTxsCount;
    }

    public Map<Integer, List<Integer>> getFinishedTxs() {
        return finishedTxs;
    }

    public void setFinishedTxs(Map<Integer, List<Integer>> finishedTxs) {
        this.finishedTxs = finishedTxs;
    }

    public Map<Integer, List<String>> getUploadData() {
        return uploadData;
    }

    public void setUploadData(Map<Integer, List<String>> uploadData) {
        this.uploadData = uploadData;
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
     * @param preTransId 当前交易的前置交易
     * @param newTx 新的合作交易
     * @return 是否添加成功
     */
    public boolean addTx(int txType, int preTransId, Transaction newTx) {
        //先判断是否是同一个流程实例
        if (!newTx.getBpId().equals(this.bpId)) {
            return false;
        }

        //Tx是第一个业务流程合作tx
        if (txType == CooperationUtil.FIRST_TX) {
            outputTxs.put(newTx.getTransId(), newTx);
            List<Integer> firstTxOutputs = new ArrayList<Integer>();
            firstTxOutputs.add(newTx.getTransId());
            inputs2Outputs.put(CooperationUtil.FIRST_TX, firstTxOutputs);
            outputs2Inputs.put(newTx.getTransId(), CooperationUtil.FIRST_TX);
            return true;
        }

        //Tx没有后续的合作
        if (txType == CooperationUtil.LAST_TX) {
            //TODO: 是不是一个协作业务流程只能有一个最后的交易
            outputTxs.put(CooperationUtil.LAST_TX, newTx);
            if (inputs2Outputs.containsKey(preTransId)) {
                List<Integer> curOutputTxs = inputs2Outputs.get(preTransId);
                curOutputTxs.add(newTx.getTransId());
                inputs2Outputs.put(preTransId, curOutputTxs);
                outputs2Inputs.put(newTx.getTransId(), preTransId);
            } else {
                return false;
            }
        }

        //Tx是申请和我合作的tx
        if (txType == CooperationUtil.INPUT_TX) {
            if (inputTxs.isEmpty()) {
                inputTxs.put(newTx.getTransId(), newTx);
                inputs2Outputs.put(CooperationUtil.FIRST_TX, new ArrayList<>());
                return true;
            }
            if (!newTx.getBpId().equals(this.bpId)) {
                return false;
            }
            if (inputTxs.containsKey(newTx.getTransId())) {
                return false;
            }
            inputTxs.put(newTx.getTransId(), newTx);
            inputs2Outputs.put(newTx.getTransId(), new ArrayList<>());
            return true;
        }
        //Tx是我申请其他人的合作的tx
        else if (txType == CooperationUtil.OUTPUT_TX) {
            if (inputTxs.containsKey(preTransId)) {
                if (!outputTxs.isEmpty() && outputTxs.containsKey(newTx.getTransId())) {
                    return false;
                } else {
                    outputTxs.put(newTx.getTransId(), newTx);
                    List<Integer> curOutputTxs = inputs2Outputs.get(preTransId);
                    curOutputTxs.add(newTx.getTransId());
                    inputs2Outputs.put(preTransId, curOutputTxs);
                    outputs2Inputs.put(newTx.getTransId(), preTransId);
                    return true;
                }

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
            Integer preTxId = outputs2Inputs.get(finishedTx.getTransId());
            if (finishedTxs.containsKey(preTxId)) {
                List<Integer> finishedTxsId = finishedTxs.get(preTxId);
                for (Integer txId : finishedTxsId) {
                    if (txId.equals(finishedTx.getTransId())) {
                        return CooperationUtil.REPEAT_ID;
                    }
                }
                finishedTxsId.add(finishedTx.getTransId());
                finishedTxs.put(preTxId, finishedTxsId);
                finishedTxsCount.put(preTxId, finishedTxsCount.get(preTxId) + 1);
            }
            else {
                List<Integer> curFinishedTxs = new ArrayList<Integer>();
                curFinishedTxs.add(finishedTx.getTransId());
                finishedTxs.put(preTxId, curFinishedTxs);
                finishedTxsCount.put(preTxId, 1);
            }
            return preTxId;
        }
        else return CooperationUtil.ERROR_USER_ID;
    }

    /**
     * 判断传入的前置事件的所有后续事件是否都已经都完成了
     * @param inputTxId 需要判断的当前节点上的前置事件
     * @return 后继事件是否都完成了
     */
    public boolean isFinished(Integer inputTxId) {
        return inputTxs.containsKey(inputTxId)
                && inputs2Outputs.containsKey(inputTxId)
                && finishedTxsCount.containsKey(inputTxId)
                && finishedTxsCount.get(inputTxId).equals(inputs2Outputs.get(inputTxId).size());
    }

    /**
     * 判断传入的前置事件的所有后续事件的数据是否已经全都传输到当前节点上
     * @param inputTxId 需要判断的当前节点上的前置事件
     * @return 后续事件数据是否都传完了
     */
    public boolean isOver(Integer inputTxId) {
        return inputTxs.containsKey(inputTxId)
                && inputs2Outputs.containsKey(inputTxId)
                && finishedTxsCount.containsKey(inputTxId)
                && finishedTxsCount.get(inputTxId).equals(uploadData.get(inputTxId).size());
    }
}
