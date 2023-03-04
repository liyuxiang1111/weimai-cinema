package com.liyuxiang.film;

import com.liyuxiang.film.config.util.FileProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.liyuxiang.film.mapper")
@EnableScheduling
@EnableConfigurationProperties({
        FileProperties.class
})
public class FilmStoreApplication {
    public static void main(String[] args){
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(FilmStoreApplication.class, args);
    }
}
