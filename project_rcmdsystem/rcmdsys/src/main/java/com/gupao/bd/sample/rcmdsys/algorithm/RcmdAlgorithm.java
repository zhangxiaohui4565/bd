/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.algorithm;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;

/**
 * @author george
 * 推荐算法：对接各种离线、在线算法
 */
public interface RcmdAlgorithm {

    public List<MoviePO> recommend(RcmdContext context);
}
