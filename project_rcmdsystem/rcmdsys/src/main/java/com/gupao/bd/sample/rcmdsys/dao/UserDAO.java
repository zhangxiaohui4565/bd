/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author george
 *
 */
@Component
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Long> getUserWatchedMovies(long userId, int fetchSize) {
        return jdbcTemplate.queryForList(
                "select movie_id from user_watched_movie where user_id = ? order by rate_time desc limit ?",
                new Object[] { userId, fetchSize }, Long.class);
    }
}
