package com.liyuxiang.film.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.liyuxiang.film.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("select * from t_comment where movie_id=#{movieId} order by calc_time desc")
    List<Comment> getComment(Integer movieId);

    @Select("select * from t_comment where movie_id=#{movieId} order by approve desc")
    List<Comment> getHotComment(Integer movieId);

    @Insert("insert into t_comment (user_id,movie_id,sc,content,approve,calc_time) values (#{userId},#{movieId},#{sc},#{content},0,now())")
    void addComment(Integer userId, Integer movieId, Integer sc, String content);

    @Select("select * from t_comment where user_id=#{userId} and movie_id=#{movieId} limit 1")
    Comment getIsComment(Integer userId, Integer movieId);

    @Update("update t_comment set sc=#{sc},content=#{content},calc_time=now() where user_id=#{userId} and movie_id=#{movieId}")
    void updateComment(Integer userId, Integer movieId, Integer sc, String content);

    @Select("select * from t_comment c where content like CONCAT('%',#{keyword},'%') " +
            "or c.user_id in (SELECT id from t_user u WHERE u.nick_name like CONCAT('%',#{keyword},'%')) " +
            "order by calc_time desc")
    List<Comment> getComments(String keyword);

    @Update("update t_comment set approve = approve+#{num} where id=#{commentId}")
    void upApproveById(Integer commentId,Integer num);

    // 统计还有几部电影未评价
    @Select("SELECT COUNT(*) FROM t_order o WHERE o.order_uid = #{userId}  and item_type = '电影票' " +
            "and item_id not in (SELECT movie_id FROM t_comment c WHERE c.user_id = #{userId})")
    Integer getNotCommentMovie(Integer userId);
}
