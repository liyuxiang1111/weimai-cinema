package com.liyuxiang.film.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.entity.AdminUser;
import com.liyuxiang.film.mapper.AdminRoleMapper;
import com.liyuxiang.film.mapper.AdminUserMapper;
import com.liyuxiang.film.mapper.CinemaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;

    public AdminUser getByUserName(String username) {
        return adminUserMapper.getByUserName(username);
    }

    public PageBean<AdminUser> getAdmins(Integer pageNum, Integer limit, String keyword) {
        PageHelper.startPage(pageNum,limit);
        List<AdminUser> adminUsers = adminUserMapper.getAdminsByKeyword(keyword);
        PageInfo<AdminUser> pageInfo = new PageInfo<>(adminUsers);
        for(AdminUser adminUser : adminUsers){
            if(adminUser.getCinemaId()!=null)
                adminUser.setCinemaNm(cinemaMapper.selectById(adminUser.getCinemaId()).getNm());
            adminUser.setRoleId(adminRoleMapper.getByUserId(adminUser.getId()).getId());
        }
        PageBean<AdminUser> page = new PageBean<>();
        page.setPc(pageInfo.getPageNum());
        page.setPs(pageInfo.getPageSize());
        page.setTr(pageInfo.getPages());
        page.setBeanList(adminUsers);
        return page;
    }

    public void updateInfo(AdminUser user) {
        adminUserMapper.updateById(user);
    }

    public void insertInfo(AdminUser user) {
        adminUserMapper.insert(user);
    }
}
