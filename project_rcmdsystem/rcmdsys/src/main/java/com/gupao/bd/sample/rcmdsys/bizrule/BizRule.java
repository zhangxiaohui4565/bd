package com.gupao.bd.sample.rcmdsys.bizrule;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;

/**
 * @author george
 *
 */
public interface BizRule {

	public List<MovieVO> applyRule(List<MovieVO> originResult);
}
