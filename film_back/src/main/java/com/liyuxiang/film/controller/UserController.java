package com.liyuxiang.film.controller;

import com.alibaba.fastjson.JSONObject;
import com.liyuxiang.film.config.shiro.WxToken;
import com.liyuxiang.film.config.util.HttpClientUtil;
import com.liyuxiang.film.config.util.Constant;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.config.util.Result;
import com.liyuxiang.film.entity.Movie;
import com.liyuxiang.film.entity.User;
import com.liyuxiang.film.entity.Vo.OrderItem;
import com.liyuxiang.film.service.CommentSerice;
import com.liyuxiang.film.service.MovieWishService;
import com.liyuxiang.film.service.OrderService;
import com.liyuxiang.film.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.jcodings.util.Hash;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MovieWishService movieWishService;
    @Autowired
    private CommentSerice commentSerice;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(UserController .class);

    @PostMapping("/wxLogin")
    public Result wxLogin(@RequestParam("code") String code,
                          @RequestParam("nickName") String nickName,
                          @RequestParam("avatarUrl") String avatarUrl,
                          @RequestParam("gender") String gender){
        //发送请求完成登录
        Map<String, String> param = new HashMap<>();
        param.put("appid", Constant.WX_LOGIN_APPID);
        param.put("secret", Constant.WX_LOGIN_SECRET);
        param.put("js_code", code);
        param.put("grant_type", Constant.WX_LOGIN_GRANT_TYPE);
        String wxResult = HttpClientUtil.doGet(Constant.WX_LOGIN_URL, param);
        System.out.println(wxResult);
        JSONObject jsonObject = JSONObject.parseObject(wxResult);
        String session_key = jsonObject.get("session_key").toString();
        String open_id = jsonObject.get("openid").toString();

        // 根据返回的user实体类，判断用户是否是新用户，不是的话，更新最新登录时间，是的话，将用户信息存到数据库
        Subject subject = SecurityUtils.getSubject();
        User user = new User();
        user.setAvatarUrl(avatarUrl);
        user.setNickName(nickName);
        if(gender.equals("1"))
            user.setGender("男");
        else
            user.setGender("女");
        user.setLastLogin(new Date());
        user.setOpenId(open_id);
        WxToken wxToken = new WxToken(user);
        subject.login(wxToken);

        // 封装返回小程序
        Map<String, String> result = new HashMap<>();
        result.put("token",subject.getSession().getId().toString());
        return new Result(result);
    }

    @GetMapping("/isAuth")
    public Result isAuth(){
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()){
            return new Result(Result.NOAUTHC,"未登录");
        }
        User user = (User) subject.getPrincipal();
        if (userService.isBaned(user.getId())) {
            return new Result(Result.NOAUTHC,"未登录");
        }
        return new Result();
    }

    @GetMapping("/wishMovie")
    public Result wishMovie() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated())
            return new Result(Result.NOAUTHC, "未登录");
        User user = (User)subject.getPrincipal();
        PageBean<Movie> pageBean = movieWishService.getWishMovieByUserId(user.getId());
        return new Result(pageBean);
    }

    @GetMapping("/getNotCommentMovie")
    public Result getNotCommentMovie() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated())
            return new Result(Result.NOAUTHC, "未登录");
        User user = (User)subject.getPrincipal();
        Integer notCommentMovie = commentSerice.getNotCommentMovie(user.getId());
        Integer orderTotal = orderService.getOrderTotal(user.getId());
        Map<String, Integer> map = new HashMap<>();
        map.put("notCommentMovie", notCommentMovie);
        map.put("orderTotal", orderTotal);
        return new Result(JSONObject.toJSON(map));
    }
}
