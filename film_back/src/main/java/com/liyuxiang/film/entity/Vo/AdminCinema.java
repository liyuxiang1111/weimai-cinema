package com.liyuxiang.film.entity.Vo;

import com.liyuxiang.film.entity.Cinema;

public class AdminCinema extends Cinema {
    private String brandName;

    public AdminCinema(Cinema cinema){
        this.setId(cinema.getId());
        this.setNm(cinema.getNm());
        this.setAddr(cinema.getAddr());
        this.setEndorse(cinema.getEndorse());
        this.setAllowrefund(cinema.getAllowRefund());
        this.setBrandId(cinema.getBrandId());
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
