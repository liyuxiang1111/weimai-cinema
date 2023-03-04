package com.liyuxiang.film.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.liyuxiang.film.entity.Banner;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerMapper extends BaseMapper<Banner> {

    @Select("select * from t_banner")
    List<Banner> getBannerList();
}
