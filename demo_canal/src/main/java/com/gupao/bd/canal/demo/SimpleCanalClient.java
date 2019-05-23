package com.gupao.bd.canal.demo;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;

import java.net.InetSocketAddress;

/**
 * 通过SingleConnector连接Canal Server(Canal Server只有一台，非HA的情况)
 *
 * 测试准备： 1）mysql -uwuchen -p123456
 *           2）insert into wuchen.user(user_name,mobile_num,register_time,city,create_time,update_time) values('gp',110,'2019-01-05 15:47:25','sh','2019-01-05 15:47:25','2019-01-05 15:47:25');
 *
 */
public class SimpleCanalClient extends AbstractCanalClient {

    public SimpleCanalClient(String destination) {
        super(destination);
    }

    public static void main(String args[]) {

        //canal server地址
        String canalServerHost = "localhost";

        //canal server监听的端口，默认是11111，可通过canal.properties修改
        int canalServerPort = 11111;

        //目标instance名称
        String destination = "example";

        //用户名/密码，留空即可，目前canal server没有开启认证
        String userName = "", password = "";

        //创建connector,连接Canal Server
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalServerHost, canalServerPort),
                destination, userName, password);

        //创建canal client,内部封装对日志的处理逻辑
        final SimpleCanalClient client = new SimpleCanalClient(destination);
        client.setConnector(connector);

        //启动canal client,实时拉取日志
        client.start();
    }
}
