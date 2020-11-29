package com.example.demo.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {
    /**
     * 获取本地ip
     */
    public static String getLocalAddress() {
        try {
            InetAddress ip4 = InetAddress.getLocalHost();
            return ip4.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
