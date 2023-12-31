package com.liyuxiang.film.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.liyuxiang.film.entity.AdminUser;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    @Select("select * from t_admin_user where username=#{username}")
    AdminUser getByUserName(String username);

    @Select("<script>" +
            "select * from t_admin_user where" +
            " name like CONCAT('%',#{keyword},'%') " +
            "</script>")
    List<AdminUser> getAdminsByKeyword(String keyword);
}
