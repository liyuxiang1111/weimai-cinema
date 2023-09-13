package com.liyuxiang.film.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.liyuxiang.film.entity.Movie;
import com.liyuxiang.film.entity.MovieWish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieWishMapper extends BaseMapper<MovieWish> {
    @Select("select * from t_movie_wish where user_id=#{userId} and movie_id=#{movieId}")
    MovieWish getByUserAndMovie(Integer userId, Integer movieId);

    @Delete("delete from t_movie_wish where id=#{id}")
    void deletById(Integer id);

    @Insert("insert into t_movie_wish (user_id,movie_id) values (#{userId},#{movieId})")
    void addWish(Integer userId, Integer movieId);

    @Select("select * from t_movie m right join t_movie_wish mw on mw.movie_id = m.id where mw.user_id = #{userid}")
    List<Movie> getWishMovieByUserId(Integer userid);
}
