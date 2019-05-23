/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.shunt;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithmFactory;
import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;
import com.gupao.bd.sample.rcmdsys.util.JsonHelper;

/**
 * @author george 
 * 从一组算法中轮询
 */
public class PollShuntStrategy extends BaseStrategy {

    private List<String> algorithms;

    private String lastRequestAlgorithm;

    /**
     * @param initParamInJson
     */
    public PollShuntStrategy(String initParamInJson) {
        super(initParamInJson);
        if (StringUtils.isEmpty(initParamInJson)) {
            throw new RuntimeException("分流策略配置参数不能为空");
        }
        try {
            algorithms = JsonHelper.fromJson(initParamInJson, List.class);
            for (String algrName : algorithms) {
                if (!RcmdAlgorithmFactory.isValidAlgorithmName(algrName)) {
                    throw new RuntimeException("不正确的算法名称：" + algrName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("分流策略配置不正确：" + initParamInJson + ". 异常信息：" + e.getMessage());
        }
    }

    @Override
    public String getRcmdAlgorithm(RcmdContext context) {
        if (lastRequestAlgorithm == null) {
            lastRequestAlgorithm = algorithms.get(0);
        } else {
            for (String algr : algorithms) {
                if (!algr.equals(lastRequestAlgorithm)) {
                    lastRequestAlgorithm = algr;
                    break;
                }
            }
        }
        return lastRequestAlgorithm;
    }

}