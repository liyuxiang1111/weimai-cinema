package com.liyuxiang.film.entity.Vo;

import lombok.Data;

@Data
public class AdminVo {
    private String token;
    private String name; // 影院的名称
    private String avatar; // 头像
    private Integer cinemaId;
}
