package com.liyuxiang.film.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liyuxiang.film.config.es.CinemaRepository;
import com.liyuxiang.film.config.exception.QQMapException;
import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.entity.CinemaBrand;
import com.liyuxiang.film.entity.Vo.CinemaFilter;
import com.liyuxiang.film.entity.Vo.CinemaVo;
import com.liyuxiang.film.entity.Vo.SelectCity;
import com.liyuxiang.film.mapper.CinemaBrandMapper;
import com.liyuxiang.film.mapper.HallTypeMapper;
import com.liyuxiang.film.config.map.QQMapUtil;
import com.liyuxiang.film.config.util.Constant;
import com.liyuxiang.film.config.util.HttpClientUtil;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.Vo.AdminCinema;
import com.liyuxiang.film.mapper.CinemaMapper;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CinemaService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private HallTypeMapper hallTypeMapper;
    @Autowired
    private CinemaBrandMapper cinemaBrandMapper;
    @Autowired
    private CinemaRepository cinemaRepository;

    public PageBean<CinemaVo> getCinemas(Integer movieId, String date, Integer pageNum, Integer limit,
                                         SelectCity cityInfo, Integer brandId, Integer serviceId, Integer hallType, String selectRegion) {
//        String key = "CinemasList,";
//        if(movieId!=null)
//            key += movieId +",";
//        if(date!=null)
//            key += date + ",";
//        if(pageNum!=null)
//            key += pageNum + ",";
//        if(limit!=null)
//            key += cityInfo.getCityName() + ",";
//        if(brandId!=null)
//            key += brandId + ",";
//        if(serviceId!=null)
//            key += serviceId + ",";
//        if(hallType!=null)
//            key += hallType + ",";
//        if(selectRegion!=null)
//            key += selectRegion;
//        PageBean<CinemaVo> cinemaVoPageBean = (PageBean<CinemaVo>)redisTemplate.opsForValue().get(key);
//        if(cinemaVoPageBean!=null)
//            return cinemaVoPageBean;
        PageHelper.startPage(pageNum,limit);
        List<Cinema> cinemaList = cinemaMapper.getCinemas(movieId,date,
                cityInfo.getLatitude(),cityInfo.getLongitude(),cityInfo.getCityName(),
                brandId,serviceId,hallType,selectRegion);
        List<CinemaVo> cinemaVoList = new ArrayList<CinemaVo>();
        BigDecimal tmp = new BigDecimal(1000);
        for(Cinema cinema : cinemaList){
            cinema.setDistance(cinema.getDistance().divide(tmp).setScale(1,BigDecimal.ROUND_HALF_UP));
            CinemaVo cinemaVo = new CinemaVo();
            cinemaVo.setCinema(cinema);
            cinemaVo.setHallTypeList(hallTypeMapper.getHallTypeByCinemaId(cinema.getId()));
            cinemaVoList.add(cinemaVo);
        }
        PageInfo pageInfo = new PageInfo(cinemaList);
        PageBean<CinemaVo> page = new PageBean<CinemaVo>();
        page.setPc(pageInfo.getPageNum());
        page.setPs(pageInfo.getSize());
        page.setTr(pageInfo.getPages());
        page.setBeanList(cinemaVoList);
//        redisTemplate.opsForValue().set(key,page);
//        redisTemplate.expire(key,10,TimeUnit.MINUTES);
        return  page;
    }

    public CinemaFilter getfilters(SelectCity cityInfo) {
        String key = "filter,"+cityInfo.getCityName();
        CinemaFilter filter = (CinemaFilter) redisTemplate.opsForValue().get(key);
        if(filter!=null)
            return filter;
        CinemaFilter cinemaFilter = new CinemaFilter();
        CinemaBrand cinemaBrand = new CinemaBrand();
        cinemaBrand.setId(-1);
        cinemaBrand.setBrand("全部");
        cinemaFilter.setCinemaBrandList(cinemaBrandMapper.getAll());
        cinemaFilter.getCinemaBrandList().add(0,cinemaBrand);
        cinemaFilter.setHallTypeList(hallTypeMapper.getAll());
        //获取
        Map<String,String> map = new HashMap<>();
        map.put("key",Constant.QQ_MAP_KEY);
        map.put("keyword",cityInfo.getCityName());
        JSONObject object = JSONObject.parseObject(HttpClientUtil.doGet(Constant.QQ_MAP_SEARCH,map));
        System.out.println();
        try {
            if(!object.getString("status").equals("0"))
                throw new QQMapException("QQ_MAP错误");
        } catch (QQMapException e) {
            e.printStackTrace();
        }
        JSONObject city = object.getJSONArray("result").getJSONArray(0).getJSONObject(0);
        map.remove("keyword");
        map.put("id",city.getString("id"));
        object = JSONObject.parseObject(HttpClientUtil.doGet(Constant.QQ_MAP_DISTRICT,map));
        try {
            if(!object.getString("status").equals("0"))
                throw new QQMapException("QQ_MAP错误");
        } catch (QQMapException e) {
            e.printStackTrace();
        }
        JSONArray tmp_district = object.getJSONArray("result").getJSONArray(0);
        List<String> district = new ArrayList<String>();
        district.add("全城");
        for(int i=0;i<tmp_district.size();i++){
            JSONObject tmp = tmp_district.getJSONObject(i);
            district.add(tmp.getString("fullname"));
        }
        cinemaFilter.setDistrict(district);
        redisTemplate.opsForValue().set(key,cinemaFilter);
        redisTemplate.expire(key,1, TimeUnit.HOURS);
        return cinemaFilter;
    }

    public Cinema getCinemaByName(String nm) {
        Cinema cinema = new Cinema();
        cinema.setNm(nm);
        return cinemaMapper.selectOne(cinema);
    }

    // 加入插入影院
    public void insertCinema(Cinema cinema) {
        cinemaMapper.insert(cinema);
        cinema.setLocation(new GeoPoint(cinema.getLatitude().doubleValue(),cinema.getLongitude().doubleValue()));
        cinemaRepository.save(cinema); // es 添加影院索引
    }

    public List<Cinema> findPage(String keyword,double latitude, double longitude, Pageable pageable) {
        // 按距离升序 ！！！！
        GeoDistanceSortBuilder distanceSortBuilder =
                new GeoDistanceSortBuilder("location", latitude, longitude);
        distanceSortBuilder.unit(DistanceUnit.KILOMETERS); // 公里
        distanceSortBuilder.order(SortOrder.ASC);
        List<Cinema> byNmLikeOrAddrLike = cinemaRepository.findByNmLikeOrAddrLike(keyword, keyword, pageable,distanceSortBuilder);
        return byNmLikeOrAddrLike;
    }


    public Cinema getCinemaById(Integer cinemaId) {
        return cinemaMapper.selectById(cinemaId);
    }

    public PageBean<AdminCinema> getCinema(Integer pageNum, Integer limit, String keyword) {
        PageHelper.startPage(pageNum,limit);
        List<Cinema> cinemaList = cinemaMapper.getCinema(keyword);
        PageBean<AdminCinema> page = new PageBean<>();
        PageInfo pageInfo = new PageInfo(cinemaList);
        page.setPc(pageInfo.getPageNum());
        page.setTr(pageInfo.getPages());
        page.setPs(pageInfo.getPageSize());
        List<AdminCinema> adminCinemas = new ArrayList<>();
        for(Cinema c : cinemaList){
            AdminCinema adminCinema = new AdminCinema(c);
            if(c.getBrandId()!=null)
                adminCinema.setBrandName(cinemaBrandMapper.getById(c.getBrandId()).getBrand());
            adminCinemas.add(adminCinema);
        }
        page.setBeanList(adminCinemas);
        return page;
    }

    public void update(Cinema cinema1) {
        Cinema cm = cinemaMapper.selectById(cinema1.getId());
        if(!cm.getAddr().equalsIgnoreCase(cinema1.getAddr())){
            //地址修改了则更新lat，lng
            cinema1 = QQMapUtil.addrToLocat(cinema1);
        }
        cinemaMapper.updateById(cinema1);
        cinemaRepository.save(cinema1);
    }
    // 删除影院
    public void deleteById(Integer cinemaId) {
        cinemaMapper.deleteById(cinemaId);
        cinemaRepository.deleteById(cinemaId);
    }

    public List<Cinema> getAllCinema() {
        return cinemaMapper.selectAll();
    }
}
