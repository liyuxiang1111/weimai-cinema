package com.liyuxiang.film.controller.admin;

import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.config.util.Result;
import com.liyuxiang.film.entity.AdminUser;
import com.liyuxiang.film.entity.Snack;
import com.liyuxiang.film.service.SnackService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/admin/snack")
@RequiresPermissions("小吃管理")
public class Admin_SnackController {

    @Autowired
    private SnackService snackService;

    @GetMapping("/getSnacks")
    public Result getSnacks(@RequestParam("pageNum") Integer pageNum,
                            @RequestParam("limit") Integer limit,
                            @RequestParam(value = "keyword",required = false) String keyword,
                            @RequestParam(value = "cinemaId",required = false) Integer cinemaId){
        Subject subject = SecurityUtils.getSubject();
        AdminUser adminUser = (AdminUser) subject.getPrincipal();
        System.out.println("===============");
        System.out.println(adminUser.getCinemaId());
        PageBean<Snack> snackPageBean = snackService.getSnack(pageNum,limit,keyword,adminUser.getCinemaId());
        return new Result(snackPageBean);
    }

    @PostMapping("/deleteSnack")
    public Result deleteSnack(@RequestBody HashMap<String,Integer> map){
        Integer snackId = map.get("snackId");
        snackService.deleteSnack(snackId);
        return new Result(snackId);
    }

    @PostMapping("/addSnack")
    public Result addSnack(@RequestBody String Snack){
        String s = StringEscapeUtils.unescapeJava(Snack);
        JSONObject object = JSONObject.fromObject(s);
        Snack snack1 = (Snack) JSONObject.toBean(object.getJSONObject("snack"),Snack.class);
        //修改
        if(snack1.getId()!=null){
            snackService.update(snack1);
        }else{
            //添加
            Snack sk = snackService.getSnackByName(snack1.getFirstTitle());
            if(sk!=null)
                return new Result(Result.ERROR,"该小吃已存在");
            snackService.insertSnack(snack1);
        }
        return new Result(snack1);
    }
}
