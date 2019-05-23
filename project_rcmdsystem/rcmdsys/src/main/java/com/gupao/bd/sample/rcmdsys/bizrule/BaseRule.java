/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.bizrule;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;

/**
 * @author george
 *
 */
public abstract class BaseRule implements BizRule {

    public abstract void loadInitParam(String initParam);
    
    @Override
    public List<MovieVO> applyRule(List<MovieVO> originResult) {
        return null;
    }

}
