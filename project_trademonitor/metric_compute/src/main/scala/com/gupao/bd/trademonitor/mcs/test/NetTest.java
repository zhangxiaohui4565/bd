package com.gupao.bd.trademonitor.mcs.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by liuhailiang on 2018/12/30.
 */
public class NetTest {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress add = InetAddress.getLocalHost();
        System.out.println("args = [" + add.getCanonicalHostName() + "]");
    }
}
