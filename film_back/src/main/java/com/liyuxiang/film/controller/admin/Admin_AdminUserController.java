package com.liyuxiang.film.controller.admin;

import com.liyuxiang.film.service.CinemaService;
import com.liyuxiang.film.entity.Cinema;
import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.config.util.Result;
import com.liyuxiang.film.entity.AdminRole;
import com.liyuxiang.film.entity.AdminUser;
import com.liyuxiang.film.entity.Vo.AdminOptions;
import com.liyuxiang.film.service.AdminRoleService;
import com.liyuxiang.film.service.AdminUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/admin/auser")
@RequiresPermissions("管理员用户管理")
public class Admin_AdminUserController {


    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private CinemaService cinemaService;
    @Autowired
    private AdminRoleService adminRoleService;

    @GetMapping("/getAdmins")
    public Result getAdmins(@RequestParam("pageNum") Integer pageNum,
                            @RequestParam("limit") Integer limit,
                            @RequestParam(value = "keyword",required = false) String keyword){
        PageBean<AdminUser> adminUserPageBean = adminUserService.getAdmins(pageNum,limit,keyword);
        return new Result(adminUserPageBean);
    }

    @GetMapping("/getOptions")
    public Result getOptions(){
        List<AdminRole> res1 = adminRoleService.getRoles();
        List<Cinema> res2 = cinemaService.getAllCinema();
        AdminOptions adminOptions = new AdminOptions();
        adminOptions.setRoles(res1);
        adminOptions.setCinemas(res2);
        return new Result(adminOptions);
    }

    @PostMapping("/updateInfo")
    public Result updateInfo(@RequestBody HashMap<String,String> map){
        Integer userId = null;
        if(map.get("userId")!=null)
            userId = Integer.parseInt(map.get("userId"));
        String name = map.get("name");
        String username = map.get("username");
        String password = map.get("password");
        // 盐值加密
        String salt = "liyuxiang799";
        Md5Hash md5Hash = new Md5Hash(password,salt,2);

        Integer roleId = Integer.parseInt(map.get("roleId"));
        Integer cinemaId = Integer.parseInt(map.get("cinemaId"));
        AdminUser user = new AdminUser();
        user.setId(userId);
        user.setName(name);
        user.setUsername(username);
        user.setPassword(md5Hash.toHex());
        user.setCinemaId(cinemaId);
        if(userId!=null)
            // 更新管理员
            adminUserService.updateInfo(user);
        else {
            user.setAvatar("https://liyuxiang.com.cn/group1/M00/00/00/rBoZWmQYD-qAUfqdAAAcjWN469A474.jpg");
            adminUserService.insertInfo(user);
            adminRoleService.insertInfo(user.getId(),roleId);
        }
        return new Result(user);
    }
}
