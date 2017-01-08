package com.model;

import java.util.List;

public class Movie {

	private Integer movieId;
	private String title;
	private List<String> genres;
	
	public List<String> getGenres() {
		return genres;
	}
	public void setgenres(List<String> genres) {
		this.genres = genres;
	}
	public Movie(Integer movieId, String title,  List<String> genres) {
		this.movieId = movieId;
		this.title = title;
        this.genres=genres;		
		
	}
	public Integer getMovieId() {
		return movieId;
	}

	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
}