package com.liyuxiang.film;

import com.alibaba.fastjson.JSON;
import com.liyuxiang.film.config.es.CinemaRepository;
import com.liyuxiang.film.config.es.MovieRepository;
import com.liyuxiang.film.config.logs.LogAnnotation;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.entity.Movie;
import com.liyuxiang.film.entity.Vo.MoviePhoto;
import com.liyuxiang.film.mapper.*;
import com.liyuxiang.film.service.CinemaService;
import com.liyuxiang.film.service.FileService;
import com.liyuxiang.film.service.MovieService;
import com.liyuxiang.film.service.MovieWishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@SpringBootTest
class MyTest {

    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private CinemaMapper cinemaMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void indexIsExists() throws IOException {
        GetIndexRequest request = new GetIndexRequest("cinema");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);// 索引是否存在
    }
    @Test
    void contextLoads() {
        List<Cinema> cinemaList = cinemaMapper.selectAll();
        for(Cinema cinema : cinemaList) {
            cinema.setLocation(new GeoPoint(cinema.getLatitude().doubleValue(), cinema.getLongitude().doubleValue()));
            cinemaRepository.index(cinema);
        }
    }

    @Test
    public void setCinemaBulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        List<Movie> movieList = movieMapper.selectAll();
        // 批量请求处理
        for (int i = 0; i < movieList.size(); i++) {
            bulkRequest.add(
                    // 这里是数据信息
                    new IndexRequest("movie") // 索引的名称默认type是_doc
                            .id(""+movieList.get(i).getId()) // 没有设置id 会自定生成一个随机id
                            .source(JSON.toJSONString(movieList.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());// ok
    }
    @Autowired
    private CinemaService cinemaService;

    @Test
    public void passwordS() {
        cinemaRepository.deleteById(8);
    }
}

