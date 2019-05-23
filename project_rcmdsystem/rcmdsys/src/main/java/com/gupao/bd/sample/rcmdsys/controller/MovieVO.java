package com.gupao.bd.sample.rcmdsys.controller;

import java.util.List;

/**
 * @author george
 *
 */
public class MovieVO {

	private String movieId;
	private String imdbId;
	private String name;
	private List<String> tags;
	private String imageUrl;
	private int year;
	
	public MovieVO(String movieId, String imdbId, String name, List<String> tags, String imageUrl, int year) {
        this.movieId = movieId;
        this.name = name;
        this.tags = tags;
        this.imageUrl = imageUrl;
        this.year = year;
        this.imdbId = imdbId;
    }

    public String getMovieId() {
		return movieId;
	}

	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
	
}
