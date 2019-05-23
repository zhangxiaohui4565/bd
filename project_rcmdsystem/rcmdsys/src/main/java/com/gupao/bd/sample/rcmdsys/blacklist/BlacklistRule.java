package com.gupao.bd.sample.rcmdsys.blacklist;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.search.SearchCriteria;

/**
 * @author george
 *
 */
public interface BlacklistRule {

    public List<SearchCriteria> getFilterConditions();
}
