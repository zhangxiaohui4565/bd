/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.controller;

import java.util.List;

/**
 * @author george
 * 推荐上下文：包含当前的用户信息
 */
public class RcmdContext {

    private Long userId;
    private Long currentMovieId;
    private List<Long> userViewedMovies;
    private RcmdType rcmdType;
    private int itemCount;
    
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getCurrentMovieId() {
        return currentMovieId;
    }
    public void setCurrentMovieId(Long currentMovieId) {
        this.currentMovieId = currentMovieId;
    }
    public List<Long> getUserViewedMovies() {
        return userViewedMovies;
    }
    public void setUserViewedMovies(List<Long> userViewedMovies) {
        this.userViewedMovies = userViewedMovies;
    }
    public RcmdType getRcmdType() {
        return rcmdType;
    }
    public void setRcmdType(RcmdType rcmdType) {
        this.rcmdType = rcmdType;
    }
    public int getItemCount() {
        return itemCount;
    }
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    
}
