package com.liyuxiang.film.entity.Vo;

import com.liyuxiang.film.entity.AdminRole;
import com.liyuxiang.film.entity.HallType;
import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.entity.Movie;

import java.util.List;

public class AdminOptions {
    private List<HallType> hallTypes;
    private List<Cinema> cinemas;
    private List<Movie> movies;
    private List<AdminRole> roles;

    public List<AdminRole> getRoles() {
        return roles;
    }

    public void setRoles(List<AdminRole> roles) {
        this.roles = roles;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<HallType> getHallTypes() {
        return hallTypes;
    }

    public void setHallTypes(List<HallType> hallTypes) {
        this.hallTypes = hallTypes;
    }

    public List<Cinema> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<Cinema> cinemas) {
        this.cinemas = cinemas;
    }
}
