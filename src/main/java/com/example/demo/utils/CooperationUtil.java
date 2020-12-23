package com.example.demo.utils;

/**
 * 定义协作过程中需要用到的一些常量
 */
public class CooperationUtil {

    //标志通讯错误
    public final static int ERROR_USER_ID = -99;

    //重复发送错误
    public final static int REPEAT_ID = -98;

    //标志不需要使用的transId
    public final static int USELESS_USER_ID = -2;

    //标志无后续合作用户
    public final static int FINISH_USER_ID = -1;

    //标志第一个合作
    public final static int FIRST_TX = 0;
    //标志无后续合作
    public final static int LAST_TX = 99;

    //用户类型，1表示发出的用户请求，2表示收到的用户请求
    public final static int OUTPUT_USER = 1;
    public final static int INPUT_USER = 2;

    //合作类型，3表示发出的合作，4表示收到的合作
    public final static int OUTPUT_TX = 3;
    public final static int INPUT_TX = 4;

    //合作事件的状态
    //发出合作
    public final static int APPLY_STATE = 5;
    //合作被通过
    public final static int CONFIRM_APPLY_STATE = 6;
    //合作完成申请上传
    public final static int UPLOAD_STATE = 7;
    //确认上传
    public final static int CONFIRM_UPLOAD_STATE = 8;

}
