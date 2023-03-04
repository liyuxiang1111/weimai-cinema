package com.liyuxiang.film.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.liyuxiang.film.entity.CommentApprove;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentApproveMapper extends BaseMapper<CommentApprove> {
    @Select("select * from t_comment_approve where comment_id=#{commentId} and user_id=#{userId}")
    CommentApprove getByTwoId(Integer commentId, Integer userId);

    @Delete("delete from t_comment_approve where comment_id=#{commentId} and user_id=#{userId}")
    void deleteByTwoId(Integer commentId, Integer userId);
}
