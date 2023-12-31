package com.liyuxiang.film.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liyuxiang.film.config.es.MovieRepository;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.Days;
import com.liyuxiang.film.entity.Movie;
import com.liyuxiang.film.entity.MovieWish;
import com.liyuxiang.film.entity.Vo.MoviePhoto;
import com.liyuxiang.film.mapper.MovieWishMapper;
import com.liyuxiang.film.mapper.TimesMapper;
import com.liyuxiang.film.mapper.DaysMapper;
import com.liyuxiang.film.mapper.MovieMapper;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MovieService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private MovieWishMapper movieWishMapper;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DaysMapper daysMapper;
    @Autowired
    private TimesMapper timesMapper;
    @Autowired
    private DataModel dataModel;
    @Autowired
    private FileService fileService;


    public PageBean<Movie> getList(Integer pageNum, Integer limit, String addr) {
        String key = "movieHot,"+pageNum+","+limit;
        PageBean<Movie> moviePageBean = (PageBean<Movie>)redisTemplate.opsForValue().get(key);
        if(moviePageBean!=null){
            return moviePageBean;
        }

        PageHelper.startPage(pageNum, limit);
        List<Movie> movieList = movieMapper.getList(addr); // 地址可以为空
        PageInfo<Movie> pageInfo = new PageInfo<Movie>(movieList);

        PageBean<Movie> page = new PageBean<>(pageInfo.getPageNum(), (int) pageInfo.getTotal(),pageInfo.getPageSize(),movieList);
        redisTemplate.opsForValue().set(key, page);
        // 过期时间为1分钟
        redisTemplate.expire(key,1, TimeUnit.MINUTES);
        return page;
    }

    public PageBean<Movie> getWait(Integer pageNum, Integer limit) {
        String key = "movieWait,"+pageNum+","+limit;
        PageBean<Movie> moviePageBean = (PageBean<Movie>)redisTemplate.opsForValue().get(key);
        if(moviePageBean!=null){
            return moviePageBean;
        }
        PageHelper.startPage(pageNum, limit);
        List<Movie> movieList = movieMapper.getWait();
        PageInfo pageInfo = new PageInfo(movieList);
        PageBean<Movie> page = new PageBean<Movie>();
        page.setPc(pageInfo.getPageNum());
        page.setPs(pageInfo.getPageSize());
        page.setTr(pageInfo.getPages());
        page.setBeanList(movieList);
        redisTemplate.opsForValue().set(key, page);
        redisTemplate.expire(key,1, TimeUnit.MINUTES);
        return page;
    }

    public List<Movie> getMoviePop() {
        String key = "moviePop";
        List<Movie> movies = (List<Movie>)redisTemplate.opsForValue().get(key);
        if(movies!=null){
            return movies;
        }
        List<Movie> movieList = movieMapper.getMoviePop();
        redisTemplate.opsForValue().set(key, movieList);
        redisTemplate.expire(key,1, TimeUnit.MINUTES);
        return movieList;
    }

    public MoviePhoto getMovieById(Integer id) {
        Movie movie = movieMapper.getMovieById(id);
        MoviePhoto moviePhoto = new MoviePhoto(movie);
        return moviePhoto;
    }

    public void updateWish(Integer userId,Integer movieId) {
        MovieWish movieWish = movieWishMapper.getByUserAndMovie(userId,movieId);
        if(movieWish!=null){
            movieWishMapper.deletById(movieWish.getId());
            movieMapper.upWish(movieId,-1);
        }else {
            movieWishMapper.addWish(userId,movieId);
            movieMapper.upWish(movieId, 1);
        }
    }

    public MovieWish getIsWish(Integer userId, Integer movieId) {
        return movieWishMapper.getByUserAndMovie(userId,movieId);
    }

    public Movie getMovieByName(String movieName) {
        return movieMapper.getMovieByName(movieName);
    }

    public void insertMovie(Movie movie) {
        if(movie.getShowst()==3)
            movie.setGlobalreleased(true);
        movieMapper.insert(movie);
        movieRepository.index(movie); // 插入document
    }

    public List<Movie> getDaysByCinemaIdAndMovieId(Integer cinemaId,Integer movieId) {
        List<Movie> movies = movieMapper.getMovieByCinemaIdAndDay(cinemaId);
        Movie movie = null;
        //查询电影的排片
        for(int i=0;i<movies.size();i++){
            List<Days> daysList = daysMapper.getDaysByCinemaIdAndMovieId(cinemaId,movies.get(i).getId());
            for(Days days : daysList){
                days.setTimes(timesMapper.getByDaysId(days.getId(),days.getDay()));
            }
            movies.get(i).setMovieDays(daysList);
            if(movieId!=null && movies.get(i).getId()==movieId)
                movie = movies.get(i);
        }
        //选择的电影放在首位
        if(movie!=null) {
            int x = movies.indexOf(movie);
            if (x > 0 && movies.size() > 1) {
                Movie tmp = movies.get(x);
                movies.set(x, movies.get(0));
                movies.set(0, tmp);
            }
        }
        return movies;
    }

    public PageBean<Movie> getMovies(Integer pageNum, Integer limit, String keyword) {
        PageHelper.startPage(pageNum,limit);
        List<Movie> movies = movieMapper.getMovies(keyword);
        for(Movie movie : movies){
            if(movie.getPhotos()!=null)
                movie.setPhotosList(movie.getPhotos().split(","));
        }
        PageBean<Movie> page = new PageBean<>();
        PageInfo pageInfo = new PageInfo(movies);
        page.setPc(pageInfo.getPageNum());
        page.setTr(pageInfo.getPages());
        page.setPs(pageInfo.getPageSize());
        page.setBeanList(movies);
        return page;
    }

    public void update(Movie inMovie) {
        if(inMovie.getShowst()==3)
            inMovie.setGlobalreleased(true);
        else
            inMovie.setGlobalreleased(false);
        movieMapper.updateById(inMovie);
        movieRepository.save(inMovie);
    }

    public void deleteById(Integer movieId) {
        // 正则
        String pattern = "(?<=8899/).*";
        Pattern r = Pattern.compile(pattern);

        Movie movie = movieMapper.selectById(movieId);
        // 删除全部剧照
        if (movie.getPhotos() != null || movie.getPhotos() != "") {
            String[] split = movie.getPhotos().split(",");
            for (String str : split) {
                Matcher matcher = r.matcher(str);
                if (matcher.find()) {
                    String group = matcher.group();
                    fileService.deleteFile(group);
                }
            }
        }
        // 删除海报
        if (movie.getImg() != null || movie.getImg() != "") {
            Matcher m = r.matcher(movie.getImg());
            String new_file = "";
            if (m.find()) {
                new_file = m.group();
            }
            fileService.deleteFile(new_file); // 删除图片
        }
        movieMapper.deleteById(movieId); // 先删除服务器的图片
        movieRepository.deleteById(movieId); // 再删除数据库和document
    }

    public List<Movie> getAllMovie() {
        return movieMapper.getAllMovie();
    }


    public List<Movie> getLikeMovieList(Integer movieId, String cat,Integer limit){
        return movieMapper.getLikeMovieList(movieId,cat,limit);
    }

    public Set<Movie> userBasedRecommender(Integer movieId, String cat, Integer userId, Integer size){
        UserSimilarity similarity  = null;
        Set<Movie> recommendItems = new HashSet<>();
        try {
            similarity = new PearsonCorrelationSimilarity(dataModel);
            NearestNUserNeighborhood  neighbor = new NearestNUserNeighborhood(2, similarity, dataModel);
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighbor,similarity);
            List<RecommendedItem> recommendations = recommender.recommend(userId, size);
            for(int i = 0 ; i < recommendations.size() ; i++) {
                RecommendedItem recommendedItem=recommendations.get(i);
                recommendItems.add(movieMapper.selectById(recommendedItem.getItemID()));
            }
        } catch (TasteException e) {
            e.printStackTrace();
        }
        if(recommendItems.size()<3){
            recommendItems.addAll(movieMapper.getLikeMovieList(movieId,cat,3-recommendItems.size()));
        }
        return recommendItems;
    }
}
