package com.liyuxiang.film.entity.Vo;

import com.liyuxiang.film.entity.Snack;
import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.entity.Movie;

import java.util.List;

public class CinemaDetail {
    private Cinema cinema;
    private List<Movie> movies;
    private List<Snack> snacks;

    public List<Snack> getSnacks() {
        return snacks;
    }

    public void setSnacks(List<Snack> snacks) {
        this.snacks = snacks;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
