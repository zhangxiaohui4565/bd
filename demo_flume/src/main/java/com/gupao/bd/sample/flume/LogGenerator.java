package com.gupao.bd.sample.flume;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * 生成样例日志，日志格式为：【IP地址 - - [时间 +0800] \"HTTP_METHOD 请求地址 HTTP/1.0\" 响应状态码 响应字节数】 
 * @author george
 *
 */
public class LogGenerator {

	private static Logger LOG = Logger.getLogger(LogGenerator.class);
	
	static List<String> responses = Arrays.asList("200","404","500","301");
	static List<String> verbs=Arrays.asList("GET","POST","DELETE","PUT");
	static List<String> urls=Arrays.asList("/list","/wp-content","/wp-admin","/explore","/search/tag/list","/app/main/posts","/posts/posts/explore","/apps/info.jsp?appID=");
	static List<String> ips = IPAddressGenerator.generateIps(1000);
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    public static void main(String[] args) throws Exception{
    	while(true){
    		String ip = ips.get(getRandom(ips.size()));
    		String dt = df.format(new Date());
    		String vrb = verbs.get(getRandom(verbs.size()));
    		int urlIndex = getRandom(urls.size());
    		String uri = urls.get(urlIndex);
    		if(urlIndex == (urls.size()-1)) {
    			uri += 1+getRandom(10000);
    		}
    		String resp = responses.get(getRandom(responses.size()));
    		int byt = 20 + getRandom(5000);
    		String log = String.format("%s - - [%s +0800] \"%s %s HTTP/1.0\" %s %s", ip,dt,vrb,uri,resp,byt);
    		LOG.info(log);
    		
    		Thread.sleep(Double.valueOf(Math.random()*1000).longValue());
    	}
    }
    
    static int getRandom(int max) {
    	Random r = new Random();
    	return r.nextInt(max);
    }
    
}
