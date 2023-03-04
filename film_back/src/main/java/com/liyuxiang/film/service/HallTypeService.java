package com.liyuxiang.film.service;

import com.liyuxiang.film.entity.HallType;
import com.liyuxiang.film.mapper.HallTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallTypeService {
    @Autowired
    private HallTypeMapper hallTypeMapper;

    public List<HallType> getHallTypes() {
        return hallTypeMapper.getAll();
    }
}
