package com.liyuxiang.film.controller.admin;

import com.liyuxiang.film.config.util.PageBean;
import com.liyuxiang.film.config.util.Result;
import com.liyuxiang.film.entity.Order;
import com.liyuxiang.film.service.OrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/admin/order")
@RequiresPermissions("订单管理")
public class Admin_OrderController {
    @Autowired
    private OrderService orderService;

    //获取订单列表
    @GetMapping("/getOrders")
    public Result getOrders(@RequestParam("pageNum") Integer pageNum,
                            @RequestParam("limit") Integer limit,
                            @RequestParam(value = "keyword",required = false) String keyword,
                            @RequestParam(value = "cinemaId",required = false)Integer cinemaId){
        PageBean<Order> orderPageBean = orderService.getOrders(pageNum,limit,keyword,cinemaId);
        return new Result(orderPageBean);
    }

    //删除订单
    @PostMapping("/deleteOrder")
    public Result deleteOrder(@RequestBody HashMap<String,Integer> map){
        Integer orderId = map.get("orderId");
        orderService.deleteOrderById(orderId);
        return new Result(orderId);
    }

}
