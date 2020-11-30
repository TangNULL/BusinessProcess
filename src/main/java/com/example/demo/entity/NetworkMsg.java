package com.example.demo.entity;

/**
 * 网络层传递的消息
 */
public class NetworkMsg {

    private static final long serialVersionUID = 1L;
    //消息类型
    private int type;
    //消息内容
    private String data;

    public NetworkMsg() {
    }

    public NetworkMsg(int type) {
        this.type = type;
    }

    public NetworkMsg(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
