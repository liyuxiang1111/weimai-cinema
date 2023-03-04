package com.liyuxiang.film.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.Banner;
import com.liyuxiang.film.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;


    public List<Banner> getBannerList() {
        return  bannerMapper.getBannerList();
    }

    public PageBean<Banner> getBanner(Integer pageNum, Integer limit, String keyword) {
        PageHelper.startPage(pageNum,limit);
        List<Banner> banners = bannerMapper.getBannerList();
        PageInfo pageInfo = new PageInfo(banners);
        PageBean<Banner> page = new PageBean<Banner>();
        page.setBeanList(banners);
        page.setTr(pageInfo.getPages());
        page.setPs(pageInfo.getSize());
        page.setPc(pageInfo.getPageNum());
        return page;
    }

    public void update(Banner banner) {
        bannerMapper.updateById(banner);
    }

    public void insert(Banner banner) {
        bannerMapper.insert(banner);
    }

    public void deleteBanner(Integer bannerId) {
        bannerMapper.deleteById(bannerId);
    }
}
