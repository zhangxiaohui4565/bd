/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.algorithm.MoviePO;
import com.gupao.bd.sample.rcmdsys.util.JsonHelper;

/**
 * 
 * 查询算法结果
 * @author george
 *
 */
@Component
public class AlgorithmDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<MoviePO> getTopNMovies(int fetchSize){
        List<Map<String, Object>> topnMovies = jdbcTemplate.queryForList(
                "select movie_id, view_count from alg_topn_movie order by view_count desc limit " + fetchSize);
        List<MoviePO> result = new ArrayList<>();
        for(Map<String, Object> record : topnMovies){
            long movieId = (Integer)record.get("movie_id");
            int viewCount = (Integer)record.get("view_count");
            
            MoviePO moviePO = new MoviePO(movieId, viewCount);
            result.add(moviePO);
        }
        return result;
    }
    
    public List<MoviePO> getWatch2WatchMovies(long srcMovieId){

        List<MoviePO> result = new ArrayList<>();
        try {
            String relatedMovies = jdbcTemplate.queryForObject(
                    "select des_movies from alg_related_movie where src_movie_id = ?", new Object[]{srcMovieId}, String.class);
            // 数据格式：[{"movieId":"3448","count":1},{"movieId":"2115","count":1}]
            if(relatedMovies != null){
                List<Map<String, Object>> movies = JsonHelper.fromJson(relatedMovies, List.class);
                for(Map<String, Object> idAndCount : movies){
                    long movieId = Long.valueOf((String)idAndCount.get("movieId"));
                    int count = ((Double)idAndCount.get("count")).intValue();
                    result.add(new MoviePO(movieId, count));
                }
            }
        } catch(EmptyResultDataAccessException e){
            // 结果为空
        }
        return result;
    }
    
    public List<MoviePO> getTopNSimilarMovies(long srcMovieId, int fetchSize){
        List<Map<String, Object>> topNMovies = jdbcTemplate.queryForList(
                "select des_movie_id, pearson from alg_ibcf_movie_similarity where src_movie_id = ?"
                + " order by pearson desc limit ?", srcMovieId, fetchSize);
        List<MoviePO> result = new ArrayList<>();
        for(Map<String, Object> record : topNMovies){
            long movieId = (Integer)record.get("des_movie_id");
            double similarity = (Double)record.get("pearson");
            
            MoviePO moviePO = new MoviePO(movieId, similarity);
            result.add(moviePO);
        }
        return result;
    }
}
