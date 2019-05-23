/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;
import com.gupao.bd.sample.rcmdsys.dao.UserDAO;
import com.gupao.bd.sample.rcmdsys.search.MovieField;
import com.gupao.bd.sample.rcmdsys.search.MovieQuery;
import com.gupao.bd.sample.rcmdsys.search.MovieSearcher;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria.Operator;

/**
 * @author george
 *
 */
@Component
public class UserService {

    @Autowired
    private UserDAO userDao;
    @Autowired
    private MovieSearcher movieSearcher;

    public List<MovieVO> getUserWatchedMovies(long userId, int fetchSize) {
        // get user viewed movie ids
        List<Long> movieIds = userDao.getUserWatchedMovies(userId, fetchSize);

        // get movie info by movie ids
        MovieQuery query = new MovieQuery();
        List<SearchCriteria> filterCriterias = new ArrayList<>();
        SearchCriteria idsCriteria = new SearchCriteria(MovieField.ID, StringUtils.join(movieIds, ","), Operator.IN);
        filterCriterias.add(idsCriteria);
        query.setFilterCriterias(filterCriterias);
        query.setStart(0);
        query.setFetchSize(movieIds.size());

        return movieSearcher.searchMovies(query);
    }
}
