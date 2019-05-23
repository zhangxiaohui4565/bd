/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.shunt;

import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;

/**
 * @author george
 * 算法分流策略：根据输入和策略选择算法
 */
public interface ShuntStrategy {

    /**
     * 根据输入的参数和策略逻辑，返回推荐算法名称
     * @return
     */
    public String getRcmdAlgorithm(RcmdContext context);
}
