/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.bizrule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;
import com.gupao.bd.sample.rcmdsys.search.MovieField;
import com.gupao.bd.sample.rcmdsys.search.MovieQuery;
import com.gupao.bd.sample.rcmdsys.search.MovieSearcher;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria.Operator;

/**
 * @author george 
 * 在推荐结果中添加某个特定的电影
 */
@Component
public class AddFeaturedMoviesRule extends BaseRule {

    @Autowired
    private MovieSearcher movieSearcher;

    private MovieQuery movieQuery;

    @Override
    public void loadInitParam(String initParam) {
        if (StringUtils.isEmpty(initParam)) {
            throw new RuntimeException("必须指定电影id");
        }
        try {
            String[] ids = initParam.split(",");
            List<SearchCriteria> filterCriterias = new ArrayList<>();
            filterCriterias.add(new SearchCriteria(MovieField.ID, initParam, Operator.IN));
            movieQuery = new MovieQuery();
            movieQuery.setFilterCriterias(filterCriterias);
            movieQuery.setFetchSize(ids.length);
            movieQuery.setStart(0);
        } catch (Exception e) {
            throw new RuntimeException("指定电影id格式错误，正确的格式为:1212,1213，错误信息：" + e.getMessage());
        }
    }

    @Override
    public List<MovieVO> applyRule(List<MovieVO> originResult) {
        List<MovieVO> result = new ArrayList<>();
        List<MovieVO> featuredMovies = movieSearcher.searchMovies(movieQuery);
        for (MovieVO movie : featuredMovies) {
            result.add(movie);
        }
        if (originResult != null) {
            result.addAll(originResult);
        }
        return result;
    }

}
