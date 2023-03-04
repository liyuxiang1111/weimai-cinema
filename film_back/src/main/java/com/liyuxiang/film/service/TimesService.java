package com.liyuxiang.film.service;

import com.liyuxiang.film.mapper.DaysMapper;
import com.liyuxiang.film.mapper.TimesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimesService {
    @Autowired
    private TimesMapper timesMapper;
    @Autowired
    private DaysMapper daysMapper;

    public void deleteMovieSchedule(Integer timesId,Integer daysId) {
        timesMapper.deleteById(timesId);
        if(daysMapper.isHashTimes(daysId)==0){
            daysMapper.deleteById(daysId);
        }
    }
}
