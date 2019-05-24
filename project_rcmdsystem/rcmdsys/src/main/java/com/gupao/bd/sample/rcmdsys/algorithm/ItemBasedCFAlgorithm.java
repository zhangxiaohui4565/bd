/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.algorithm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;
import com.gupao.bd.sample.rcmdsys.dao.AlgorithmDAO;

/**
 * @author george
 *
 */
@Component
public class ItemBasedCFAlgorithm implements RcmdAlgorithm {

    @Autowired
    private AlgorithmDAO dao;
    
    private final static int FETCH_SIZE = 100;
    
    @Override
    public List<MoviePO> recommend(RcmdContext context) {
        return dao.getTopNSimilarMovies(context.getCurrentMovieId(), FETCH_SIZE);
    }

}