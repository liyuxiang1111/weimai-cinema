package com.liyuxiang.film.controller.admin;

import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.config.util.Result;
import com.liyuxiang.film.entity.*;
import com.liyuxiang.film.entity.Vo.AdminOptions;
import com.liyuxiang.film.entity.Vo.MovieSchedule;
import com.liyuxiang.film.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/admin/schedule")
@RequiresPermissions("排片管理")
public class Admin_DaysAndTimesController {

    @Autowired
    private DaysService daysService;
    @Autowired
    private TimesService timesService;
    @Autowired
    private MovieService moviceService;
    @Autowired
    private CinemaService cinemaService;
    @Autowired
    private HallService hallService;

    @GetMapping("/getMovieSchedule")
    public Result getMovieSchedule(@RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("limit") Integer limit,
                                   @RequestParam("keyword") String keyword,
                                   @RequestParam(value = "cinemaId",required = false) Integer cinemaId){
        Subject subject = SecurityUtils.getSubject();
        AdminUser adminUser = (AdminUser) subject.getPrincipal();
        PageBean<MovieSchedule> movieSchedulePageBean = daysService.getMovieSchedule(pageNum,limit,keyword,adminUser.getCinemaId());
        return new Result(movieSchedulePageBean);
    }

    @PostMapping("/addScheduleInfo")
    public Result addScheduleInfo(@RequestBody HashMap<String,String> map) throws ParseException {
        Integer movieId = Integer.parseInt(map.get("movieId"));
        Integer cinemaId = Integer.parseInt(map.get("cinemaId"));
        Integer hallId = Integer.parseInt(map.get("hallId"));
        SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyy-MM-dd" );
        Date showDate = sdf1.parse(map.get("showDate"));
        SimpleDateFormat sdf2 = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        Date showTime = sdf2.parse(map.get("showDate") + " " + map.get("showTime"));
        BigDecimal price = new BigDecimal(map.get("price"));
        Times times = daysService.addScheduleInfo(movieId,cinemaId,showDate,hallId,showTime,price);
        if(times==null)
            return new Result(Result.ERROR,"无法添加排片");
        return new Result(times);
    }

    @PostMapping("/deleteMovieSchedule")
    public Result deleteMovieSchedule(@RequestBody HashMap<String,Integer> map){
        Integer timesId = map.get("timesId");
        Integer daysId = map.get("daysId");
        timesService.deleteMovieSchedule(timesId,daysId);
        return new Result(timesId);
    }

    @GetMapping("/getOptions")
    public Result getOptions(){
        List<Movie> movieList = moviceService.getAllMovie();
        List<Cinema> cinemaList = new ArrayList<Cinema>();
        Subject subject = SecurityUtils.getSubject();
        AdminUser adminUser = (AdminUser) subject.getPrincipal();
        if (adminUser.getCinemaId() != null) {
            cinemaList.add(cinemaService.getCinemaById(adminUser.getCinemaId()));
        } else {
            cinemaList = cinemaService.getAllCinema();
        }
        AdminOptions options = new AdminOptions();
        options.setCinemas(cinemaList);
        options.setMovies(movieList);
        return new Result(options);
    }

    @GetMapping("/getHallByCinema")
    public Result getHallByCinema(@RequestParam("cinemaId") Integer cinemaId){
        List<Hall> halls = hallService.getHallByCinema(cinemaId);
        return new Result(halls);
    }

}
