package com.liyuxiang.film.config.es;

import com.liyuxiang.film.entity.Cinema;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CinemaRepository extends ElasticsearchRepository<Cinema,Integer> {
    List<Cinema> findByNmLikeOrAddrLike(String key1, String k2, Pageable pageable, GeoDistanceSortBuilder distanceSortBuilder);
}
