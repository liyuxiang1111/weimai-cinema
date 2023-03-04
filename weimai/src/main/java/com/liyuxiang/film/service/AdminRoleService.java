package com.liyuxiang.film.service;

import com.liyuxiang.film.entity.AdminRole;
import com.liyuxiang.film.mapper.AdminRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminRoleService {
    @Autowired
    private AdminRoleMapper adminRoleMapper;


    public List<AdminRole> getRoles() {
        return adminRoleMapper.getRoles();
    }

    public void insertInfo(Integer id, Integer roleId) {
        adminRoleMapper.insertInfo(id,roleId);
    }
}
