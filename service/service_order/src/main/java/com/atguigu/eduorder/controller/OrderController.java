package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author Russell
 * @since 2023-07-23
 */
@RestController
@RequestMapping("/eduorder/order")
@CrossOrigin

public class OrderController {
    @Autowired
    private OrderService orderService;

    //1.生成订单
    @PostMapping("createOder/{courseId}")
    public R saveOrder(
            @PathVariable String courseId,
            HttpServletRequest request) {
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        if (memberId.equals("")) return R.error().code(28004).message("请登录");
        //创建订单,返回订单号
        String orderNo = orderService.createOrders(
                courseId,
                JwtUtils.getMemberIdByJwtToken(request));
        return R.ok().data("orderId", orderNo);
    }

    //2.根据订单号查询订单信息
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable("orderId") String orderId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item", order);
    }

    //3.根据课程id和用户id查询订单表status值是否为1(已支付)
    @GetMapping("isBuyCourse/{courseId}/{memberId}")
    public boolean isBuyCourse(
            @PathVariable String courseId,
            @PathVariable String memberId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("member_id", memberId);
        wrapper.eq("status", 1); //1代表已经支付
        int count = orderService.count(wrapper);
        if (count > 0) { //已经支付
            return true;
        }
        //未支付
        return false;
    }


}

