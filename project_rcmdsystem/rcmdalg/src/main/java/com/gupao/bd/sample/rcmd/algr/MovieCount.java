package com.gupao.bd.sample.rcmd.algr;

public class MovieCount implements Comparable<MovieCount> {

	private String movieId;
	private int count;

	public String getMovieId() {
		return movieId;
	}

	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public MovieCount(String movieId, int count) {
		super();
		this.movieId = movieId;
		this.count = count;
	}

	@Override
	public int compareTo(MovieCount o) {
		if (o == null) {
			return -1;
		} else {
			return o.getCount() - this.getCount();
		}
	}

}
