package com.liyuxiang.film.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.liyuxiang.film.entity.Times;
import com.liyuxiang.film.entity.Cinema;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TimesMapper extends BaseMapper<Times> {
    @Select("select * from t_times where days_id=#{dasId} and to_days(start_time)=to_days(#{day}) and start_time>now() order by start_time")
    List<Times> getByDaysId(Integer dasId, Date day);

    // 相关id --> 影厅id --> 影院id --> 根据影院id查询电影院
    @Select("<script>" +
            "select * from t_cinema where id in " +
            "(select cinema_id from t_hall where id in (" +
            "select hall_id from t_times where id=#{relateId}))" +
            "</script>")
    Cinema getCiemaId(Integer relateId);

    @Update("update t_times set start_time=DATE_ADD(start_time,INTERVAL 1 DAY)")
    void updateStart();

    @Select("<script>" +
            "select * from t_times where days_id in" +
            "(select id from t_days where movie_id in " +
            "(select id from t_movie where nm like CONCAT('%',#{keyword},'%')) " +
            "<if test='cinemaId!=null'>" +
            "and cinema_id=#{cinemaId} " +
            "</if>)" +
            "order by start_time desc" +
            "</script>")
    List<Times> getAll(String keyword, Integer cinemaId);
}
