package com.liyuxiang.film.config.es;

import com.liyuxiang.film.config.redis.RedisConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(RedisConfig.class)
public class ElasticSearchConfig {
    // 注册 rest高级客户端
    // bean 配置 <bean id="" name=""> name几时取别名
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("47.115.204.84",9200,"http")
                )
        );
        return client;
    }
}