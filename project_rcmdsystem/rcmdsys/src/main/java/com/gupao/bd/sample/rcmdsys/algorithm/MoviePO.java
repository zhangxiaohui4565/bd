/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.algorithm;

/**
 * @author george
 *
 */
public class MoviePO {

    private long movieId;
    private double score;
    
    public MoviePO(long movieId, double score) {
        this.movieId = movieId;
        this.score = score;
    }
    
    public long getMovieId() {
        return movieId;
    }
    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
}
