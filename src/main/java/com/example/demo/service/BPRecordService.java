package com.example.demo.service;

/**
 * 生成联盟链并且从联盟链上传记录的数据
 */
public interface BPRecordService {
    boolean downloadPhase();  //生成联盟链
    boolean generatePhase();  //联盟链扩展
    boolean uploadPhase();  //联盟链上传
}
