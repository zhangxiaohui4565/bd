/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.shunt;

/**
 * @author george
 * 分流策略基类，所有实现必须实现接收json数据格式配置的构造器
 */
public abstract class BaseStrategy implements ShuntStrategy {

    public BaseStrategy(String initParamInJson){
        
    }

}
