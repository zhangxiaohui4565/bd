package com.gupao.bd.eos.client.util;

import java.net.*;
import java.util.Enumeration;

/**
 * 网络工具类
 */
public class NetworkUtils {
    private static String localIp;
    /**
     * 获取本机IP v4地址
     * 如果有多个IP则返回其中一个IPv4
     * 找不到则返回127.0.0.1
     * @return 本机IP
     */
    public static String getLocalIpV4() {
        //FIXME : 有多个IP地址时，取到的IP地址不正确
        if (null != localIp) { // 使用已经获取的IP，服务运行时IP变化，则有问题
            return localIp;
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress()) {
                        continue;
                    }
                    if (addr instanceof Inet4Address) {
                        localIp = addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // ignore
        }
        if (null == localIp) {
            localIp = "127.0.0.1";
        }

        return localIp;
    }

    public static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }

    public static String getHostName() {
        if (System.getenv("COMPUTERNAME") != null) {
            return System.getenv("COMPUTERNAME");
        } else {
            return getHostNameForLiunx();
        }
    }
}
