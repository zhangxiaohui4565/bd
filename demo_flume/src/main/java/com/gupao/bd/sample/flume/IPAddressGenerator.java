package com.gupao.bd.sample.flume;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 随机IP地址生成工具类
 * @author george
 *
 */
public class IPAddressGenerator {

	/**
	 * 生成指定数量的随机ip地址
	 * @param count
	 * @return
	 */
	public static List<String> generateIps(int count){
		Set<String> addresses = new HashSet<String>();

		while (addresses.size() < count) {
			String ip = rand(20, 255) + "." + rand(0, 255) + "." + rand(0, 255) + "." + rand(0, 255);
			addresses.add(ip);
		}

		return new ArrayList<String>(addresses);
	}

	private static int rand(int a, int b) {
		return ((int) ((b - a + 1) * Math.random() + a));
	}
}

