package com.liyuxiang.film.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.Movie;
import com.liyuxiang.film.mapper.MovieWishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieWishService {
    @Autowired
    private MovieWishMapper movieWishMapper;

    public PageBean<Movie> getWishMovieByUserId(Integer userId) {
        PageHelper.startPage(1,0);
        List<Movie> movies = movieWishMapper.getWishMovieByUserId(userId);
        PageInfo<Movie> pageInfo = new PageInfo<>(movies);
        PageBean<Movie> pageBean = new PageBean<>(pageInfo.getPageNum(), pageInfo.getSize(), pageInfo.getPageSize(), movies);
        return pageBean;
    }
}
