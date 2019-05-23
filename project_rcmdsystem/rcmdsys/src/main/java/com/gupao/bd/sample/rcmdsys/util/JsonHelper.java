/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.util;

import com.google.gson.Gson;

/**
 * @author george
 *
 */
public class JsonHelper {
    
    public static <T> T fromJson(String json, Class<T> classOfT){
        Gson gson = new Gson();
        return gson.fromJson(json, classOfT);
    }
    
    public static String fromJson(Object jsonObj){
        Gson gson = new Gson();
        return gson.toJson(jsonObj);
    }
}
