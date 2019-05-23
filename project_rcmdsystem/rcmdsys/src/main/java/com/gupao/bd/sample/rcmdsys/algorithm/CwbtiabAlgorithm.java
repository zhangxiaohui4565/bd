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
 * 【看了又看，买了又买】算法
 */
@Component
public class CwbtiabAlgorithm implements RcmdAlgorithm {

    @Autowired
    private AlgorithmDAO dao;
    
    @Override
    public List<MoviePO> recommend(RcmdContext context) {
        return dao.getWatch2WatchMovies(context.getCurrentMovieId());
    }

}
