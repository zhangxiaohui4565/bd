/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.search;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;

/**
 * @author george
 *
 */
public interface MovieSearcher {

    public List<MovieVO> searchMovies(MovieQuery query);
}
