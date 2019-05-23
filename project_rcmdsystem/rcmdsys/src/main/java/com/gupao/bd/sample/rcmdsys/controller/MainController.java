/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gupao.bd.sample.rcmdsys.search.MovieField;
import com.gupao.bd.sample.rcmdsys.search.MovieQuery;
import com.gupao.bd.sample.rcmdsys.search.MovieSearcher;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria.Operator;
import com.gupao.bd.sample.rcmdsys.service.RcmdResult;
import com.gupao.bd.sample.rcmdsys.service.RcmdService;
import com.gupao.bd.sample.rcmdsys.service.UserService;

/**
 * @author george
 *
 */
@Controller
public class MainController {

    @Autowired
    private RcmdService rcmdService;
    @Autowired
    private UserService userService;
    @Autowired
    private MovieSearcher movieSearcher;
    
    private final static int WATCHED_MOVIE_FETCH_SIZE = 10;
    private final static int DEFAULT_ITEM_COUNT = 10;
    private final static int SEARCH_MOVIE_FETCH_SIZE = 10;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String homepage(Model model, @RequestParam(required=false, name="userId") String userId) {
        RcmdContext context = new RcmdContext();
        context.setItemCount(DEFAULT_ITEM_COUNT);
        List<MovieVO> watchedMovies = null;
        if(StringUtils.isNotEmpty(userId)){
            context.setUserId(Long.valueOf(userId));
            context.setRcmdType(RcmdType.USER_HOMEPAGE);
            
            watchedMovies = userService.getUserWatchedMovies(Long.valueOf(userId), WATCHED_MOVIE_FETCH_SIZE);
        } else {
            context.setRcmdType(RcmdType.VISITOR_HOMEPAGE);
        }
        RcmdResult rcmdMovies = rcmdService.recommend(context);
        model.addAttribute("rcmdMovies", rcmdMovies.getRcmdItems());
        model.addAttribute("watchedMovies", watchedMovies);
        
        return "home";
    }

    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.GET)
    public String viewMovie(Model model, @RequestParam(required=false, name="userId") String userId, @PathVariable String movieId) {
        
        RcmdContext context = new RcmdContext();
        context.setItemCount(DEFAULT_ITEM_COUNT);
        if(StringUtils.isNotEmpty(userId)){
            context.setUserId(Long.valueOf(userId));
        }
        context.setRcmdType(RcmdType.SINGLE_MOVIE_PAGE);
        context.setCurrentMovieId(Long.valueOf(movieId));
        
        RcmdResult rcmdMovies = rcmdService.recommend(context);
        model.addAttribute("rcmdMovies", rcmdMovies.getRcmdItems());
        
        MovieQuery query = new MovieQuery();
        List<SearchCriteria> filterCriterias = new ArrayList<>();
        filterCriterias.add(new SearchCriteria(MovieField.ID, movieId, Operator.EQUAL));
        query.setFilterCriterias(filterCriterias);
        query.setFetchSize(1);
        List<MovieVO> currentMovie = movieSearcher.searchMovies(query);
        model.addAttribute("currentMovie", currentMovie.get(0));
        model.addAttribute("rcmdAlgorithm", rcmdMovies.getRcmdAlgorithm());
        
        return "movie";
    }

    @RequestMapping(value = "/dosearch", method = RequestMethod.GET)
    public String searchByName(Model model, @RequestParam(required=false, name="keyword") String keyword) {
        MovieQuery query = new MovieQuery();
        List<SearchCriteria> searchCriterias = new ArrayList<>();
        searchCriterias.add(new SearchCriteria(MovieField.NAME, keyword, Operator.CONTAIN));
        query.setSearchCriterias(searchCriterias);
        query.setFetchSize(SEARCH_MOVIE_FETCH_SIZE);
        List<MovieVO> searchResult = movieSearcher.searchMovies(query);
        model.addAttribute("searchMovies", searchResult);
        
        return "search";
    }
}
