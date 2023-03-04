package com.liyuxiang.film.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.Hall;
import com.liyuxiang.film.entity.Vo.AdminHall;
import com.liyuxiang.film.mapper.HallMapper;
import com.liyuxiang.film.mapper.SeatMapper;
import com.liyuxiang.film.mapper.CinemaMapper;
import com.liyuxiang.film.mapper.HallTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HallService {

    @Autowired
    private HallMapper hallMapper;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private HallTypeMapper hallTypeMapper;
    @Autowired
    private SeatMapper seatMapper;

    public PageBean<AdminHall> getHalls(Integer pageNum, Integer limit, String keyword, Integer cinemaId) {
        PageHelper.startPage(pageNum,limit);
        List<Hall> halls = hallMapper.getHalls(keyword,cinemaId);
        List<AdminHall> adminHalls = new ArrayList<>();
        for(Hall hall : halls){
            AdminHall adminHall = new AdminHall(hall);
            adminHall.setCinemaNm(cinemaMapper.selectById(hall.getCinemaId()).getNm());
            adminHall.setHallType(hallTypeMapper.selectById(hall.getHallTypeId()).getHalltype());
            adminHall.setSeats(seatMapper.getSeatByHallId(hall.getId()));
            adminHalls.add(adminHall);
        }
        PageInfo pageInfo = new PageInfo(halls);
        PageBean<AdminHall> page = new PageBean<>();
        page.setTr(pageInfo.getPages());
        page.setPc(pageInfo.getPageNum());
        page.setPs(pageInfo.getPageSize());
        page.setBeanList(adminHalls);
        return page;
    }

    public Hall getHallById(Integer hallId) {
        return hallMapper.selectById(hallId);
    }

    public void updateHall(Hall hall) {
        hallMapper.updateById(hall);
    }

    public void deleteHall(Integer hallId) {
        hallMapper.deleteById(hallId);
    }

    public void insertHall(Hall hall) {
        hallMapper.insert(hall);
    }

    public List<Hall> getHallByCinema(Integer cinemaId) {
        return hallMapper.getHallByCinema(cinemaId);
    }
}
