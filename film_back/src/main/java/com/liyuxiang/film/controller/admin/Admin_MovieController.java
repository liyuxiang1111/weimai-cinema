package com.liyuxiang.film.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.liyuxiang.film.service.FileService;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.config.util.Result;
import com.liyuxiang.film.entity.Movie;
import com.liyuxiang.film.service.MovieService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/movie")
public class Admin_MovieController {
    private final static Logger logger = LoggerFactory.getLogger(Admin_MovieController.class);

    @Autowired
    private MovieService moviceService;
    @Autowired
    private FileService fileService;

    @PostMapping("/addMoive")
    @RequiresPermissions("电影管理")
    public Result addMovie(@RequestBody String movie){
        String s = StringEscapeUtils.unescapeJava(movie);
//        JSONObject object = JSONObject.fromObject(s);
//        Movie movie1 = (Movie) JSONObject.toBean(object.getJSONObject("movie"),Movie.class);
        Movie movie1 = JSONObject.parseObject(s).getJSONObject("movie").toJavaObject(Movie.class);
        // 处理剧照
        if (movie1.getPhotosList() != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for(String ss : movie1.getPhotosList()){
                stringBuffer.append(ss+",");
            }
            movie1.setPhotos(stringBuffer.substring(0,stringBuffer.length()-1));
        }
        //修改
        if(movie1.getId()!=null){
            moviceService.update(movie1);
        }else{
            //添加
            Movie mv = moviceService.getMovieByName(movie1.getNm());
            if(mv!=null)
                return new Result(Result.ERROR,"该电影已存在");
            moviceService.insertMovie(movie1);
        }
        return new Result(movie);
    }

    // 删除电影
    @PostMapping("/deleteMovie")
    @RequiresPermissions("电影管理")
    public Result deleteMovie(@RequestBody HashMap<String, String> map){
        Integer movieId = Integer.parseInt(map.get("movieId"));
        moviceService.deleteById(movieId);
        return new Result(movieId);
    }

    @GetMapping("/getMovies")
    @RequiresPermissions("电影管理")
    public Result getMovies(@RequestParam("pageNum") Integer pageNum,
                           @RequestParam("limit") Integer limit,
                           @RequestParam(value = "keyword",required = false) String keyword){
        PageBean<Movie> moviePageBean = moviceService.getMovies(pageNum,limit,keyword);
        return new Result(moviePageBean);
    }

    @PostMapping("/upLoadFile")
    @RequiresPermissions("上传文件")
    public Result upLoadFile(@RequestParam(value = "img",required = false) MultipartFile img,
                             @RequestParam(value = "videoImg",required = false) MultipartFile videoImg,
                             @RequestParam(value = "video",required = false) MultipartFile video){
        Map<String,String> res = new HashMap<>();
        if(img!=null){
            String fileNameImg = fileService.storeFile(img);
            res.put("img",fileNameImg);
        }
        if(videoImg!=null){
            String fileNameVideoImg = fileService.storeFile(videoImg);
            res.put("videoImg",fileNameVideoImg);
        }
        if(video!=null){
            String fileNameVideo = null;
            try {
                fileNameVideo = fileService.uploadMp4(video);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            res.put("video",fileNameVideo);
        }
        return new Result(res);
    }

    @PostMapping("/deletFile")
    @RequiresPermissions("上传文件")
    public Result deleteFile(@RequestBody HashMap<String, String> map) {
        String img = map.get("img");
        String videoImg = map.get("videoImg");
        String video = map.get("video");
        if(img!= "" || img != null){
            boolean deleteImg = fileService.deleteFile(img);
            return new Result(deleteImg);
        }
        if(videoImg!= "" || videoImg != null){
            boolean deleteVideoImg = fileService.deleteFile(videoImg);
            return new Result(deleteVideoImg);
        }
        if(video!= "" || video != null){
            boolean deleteMp4 = fileService.deleteMp4(video);
            return new Result(deleteMp4);
        }
        return new Result(null);
    }
}
