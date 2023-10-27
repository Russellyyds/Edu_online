package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduorder.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author Russell
 * @since 2023-07-23
 */
@RestController
@RequestMapping("/eduorder/paylog")
@CrossOrigin
public class PayLogController {
    @Autowired
    private PayLogService payLogService;

    //1.生成微信支付二维码
//参数是订单号
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo) {
        //业务层返回的信息中包含二维码地址,还有一些其它我们需要的信息
        Map map = payLogService.createNative(orderNo);
        if (map == null) {
            return R.error().message("支付出错了");
        }
        //map不为空,那就从map中获取订单状态
        if (map.get("trade_state").equals("SUCCESS")) {
            //向t_pay_log(支付日志记录表)添加一条记录
            //并且修改t_order(订单表)的status字段为1(已支付)
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中").code(25000);

    }

}

