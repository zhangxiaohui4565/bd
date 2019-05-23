/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.blacklist;

import java.util.ArrayList;
import java.util.List;

import com.gupao.bd.sample.rcmdsys.search.MovieField;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria;

/**
 * @author george
 * 过滤色情电影：tag: porn
 */
public class FilterPornMovieRule implements BlacklistRule{

    private final static List<SearchCriteria> CONDITIONS = new ArrayList<>();
    static {
        CONDITIONS.add(new SearchCriteria(MovieField.TAGS, "porn", SearchCriteria.Operator.NOT_EQUAL));
    }
    
    @Override
    public List<SearchCriteria> getFilterConditions() {
        return CONDITIONS;
    }

}
