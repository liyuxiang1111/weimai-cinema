package com.liyuxiang.film.config.es;

import com.liyuxiang.film.entity.Cinema;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CinemaRepository extends ElasticsearchRepository<Cinema,Integer> {


}
