package com.gupao.bd.spark_streaming.demo.util;

import java.io.*;
import java.net.Socket;

/**
 * 向Server发送数据
 */
public class SocketClient {
    public static final String IP_ADDR = "localhost";
    public static final int PORT = 9999;

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket client = new Socket(IP_ADDR, PORT);
        int maxCnt = 10000;

        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        for (int i = 0; i < maxCnt; i++) {
            String str = "hello" + (i % 10) + "\n";
            System.out.println(str);
            out.writeUTF(str);
            out.flush();
            Thread.sleep(1000);
        }

        out.close();
        client.close();
    }
}
