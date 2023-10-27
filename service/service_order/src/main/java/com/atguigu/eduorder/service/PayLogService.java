package com.atguigu.eduorder.service;

import com.atguigu.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author Russell
 * @since 2023-07-23
 */
public interface PayLogService extends IService<PayLog> {

    Map createNative(String orderNo);

    //根据订单号查询订单支付状态
    Map<String, String> queryPayStatus(String orderNo);

    //向t_pay_log(支付日志记录表)添加一条记录
//并且修改t_order(订单表)的status字段为1(已支付)
    void updateOrderStatus(Map<String, String> map);
}
