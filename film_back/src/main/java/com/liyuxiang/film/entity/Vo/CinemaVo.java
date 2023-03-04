package com.liyuxiang.film.entity.Vo;

import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.entity.HallType;

import java.util.List;

public class CinemaVo {
    private Cinema cinema;
    private List<HallType> HallTypeList;

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public List<HallType> getHallTypeList() {
        return HallTypeList;
    }

    public void setHallTypeList(List<HallType> hallTypeList) {
        HallTypeList = hallTypeList;
    }
}
