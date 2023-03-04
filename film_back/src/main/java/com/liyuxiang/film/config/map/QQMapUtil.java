package com.liyuxiang.film.config.map;

import com.alibaba.fastjson.JSONObject;
import com.liyuxiang.film.config.exception.QQMapException;
import com.liyuxiang.film.config.util.HttpClientUtil;
import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.config.util.Constant;
import org.apache.commons.lang.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QQMapUtil {

    public static Cinema addrToLocat(Cinema cinema){
        Map<String,String> map = new HashMap<>();
        map.put("key", Constant.QQ_MAP_KEY);
        map.put("address",cinema.getAddr());
        String qq_res = HttpClientUtil.doGet(Constant.QQ_MAP_TO,map);
        JSONObject object = JSONObject.parseObject(StringEscapeUtils.unescapeJava(qq_res));
        try {
            if(!object.getString("status").equals("0")) {
                //address=北京市海淀区上地十街10号&output=json&ak=您的ak&callback=showLocation
                Map<String,String> map2 = new HashMap<>();
                map2.put("ak", Constant.BAIDU_MAP_AK);
                map2.put("address",cinema.getAddr());
                map2.put("output","json");
                String baidu_res = HttpClientUtil.doGet(Constant.BAIDU_MAP_TO,map2);
                object = JSONObject.parseObject(StringEscapeUtils.unescapeJava(baidu_res));
                if(!object.getString("status").equals("0")) {
                    throw new QQMapException("地址解析错误");
                }
            }
        } catch (QQMapException e) {
            e.printStackTrace();
        }
        JSONObject result = object.getJSONObject("result");
        if(result.getJSONObject("address_components") != null) {
            String[] res = new String[3];
            JSONObject tmp = result.getJSONObject("address_components");
            res[0] = "";
            if (!cinema.getAddr().contains(tmp.getString("province")))
                res[0] += tmp.getString("province");
            if (!cinema.getAddr().contains(tmp.getString("city")))
                res[0] += tmp.getString("city");
            res[0] += cinema.getAddr();
            res[1] = result.getJSONObject("location").getString("lat");
            res[2] = result.getJSONObject("location").getString("lng");
            cinema.setAddr(res[0]);
            cinema.setLatitude(new BigDecimal(res[1]));
            cinema.setLongitude(new BigDecimal(res[2]));
        }
        else {
            cinema.setLatitude(new BigDecimal(result.getJSONObject("location").getString("lat")));
            cinema.setLongitude(new BigDecimal(result.getJSONObject("location").getString("lng")));
        }

        return cinema;
    }
}
