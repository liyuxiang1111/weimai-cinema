package com.liyuxiang.film;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.liyuxiang.film.mapper")
@EnableScheduling
public class FilmStoreApplication {
    public static void main(String[] args){
        System.setProperty("es.set.netty.runtime.available.processors","true");
        SpringApplication.run(FilmStoreApplication.class, args);
    }
}
