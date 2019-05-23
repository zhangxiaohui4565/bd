package com.gp.bd.sample.storm.utils;

import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

   private static Properties config=null;
   private static String configFile="storm-example.properties";

   public static void loadProperties(String fileName){
       config = new Properties();
       InputStream in ;
       try {
           in = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
           config.load(in);
       } catch (Exception e) {
           e.fillInStackTrace();
           System.out.println("加载"+fileName+"失败,"+e.getStackTrace());
       }
   }


    public static String getPropertyValue(String key) {
        String value = StringUtils.EMPTY;
        if(config == null){
            loadProperties(configFile);
        }
        try {
            value = config.getProperty(key);
        } catch (Exception e) {
            System.out.println(String.format("提取%s属性值异常!", key)+ e);
        }
        return value != null ? value.trim() : StringUtils.EMPTY;
    }


}
