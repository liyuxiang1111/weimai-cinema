package com.liyuxiang.film.config.es;

import com.liyuxiang.film.entity.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MovieRepository extends ElasticsearchRepository<Movie,Integer> {

    List<Movie> findByNmLikeOrDirLikeOrCatLikeOrDraLikeOrStarContains(String key1,String key2,String key3,String key4,String key5);
}
