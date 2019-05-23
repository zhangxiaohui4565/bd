package com.gupao.bd.canal.demo;


import com.google.code.or.OpenReplicator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * 通过OpenReplicator直连MySQL进行binlog的解析和消费（canal底层使用了OpenReplicator）
 */
public class OpenReplicatorDemo {

    public static void main(String[] args) throws Exception {

        final OpenReplicator or = new OpenReplicator();
        or.setHost("todo");
        or.setPort(-1);
        or.setUser("todo");
        or.setPassword("todo");
        or.setServerId(6789);
        or.setBinlogPosition(4); //        开头4个字节binlog magic number：fe 62 69 6e，即.bin
        or.setBinlogFileName("mysql-bin.000001");
        or.setBinlogEventListener(event -> {
            System.out.println(event.getHeader().getEventType() + " >>>>>>>>>>>>>>> ");
            System.out.println(event.toString());
        });
        or.start();

        System.out.println("press 'q' to stop");
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (line.equals("q")) {
                or.stop(3, TimeUnit.SECONDS);
                break;
            }
        }
    }

}
