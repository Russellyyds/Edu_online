package com.atguigu.eduorder.service.impl;

import com.atguigu.commonutils.ordervo.CourseWebVoOrder;
import com.atguigu.commonutils.ordervo.UcenterMemberOrder;
import com.atguigu.eduorder.client.EduClient;
import com.atguigu.eduorder.client.UcenterClient;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.mapper.OrderMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.utils.OrderNoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author Russell
 * @since 2023-07-23
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduClient eduClient;
    @Autowired
    private UcenterClient ucenterClient;

    @Override
    public String createOrders(String courseId, String memberId) {

        //通过远程调用 用户id 获取用户信息
        UcenterMemberOrder userInfoOrder = ucenterClient.getUserInfoOrder(memberId);

        //通过远程调用根据id获取课程信息
        CourseWebVoOrder courseInfoOrder = eduClient.getCourseInfoOrder(courseId);
        //创建订单对象
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo()); //订单号
        order.setCourseId(courseId); //课程id
        order.setCourseTitle(courseInfoOrder.getTitle()); //课程名称
        order.setCourseCover(courseInfoOrder.getCover()); //课程封面
        order.setTeacherName(courseInfoOrder.getTeacherName()); //课程所属讲师
        order.setTotalFee(courseInfoOrder.getPrice()); //订单金额(也就是课程价格)
        order.setMemberId(memberId); //用户id
        order.setMobile(userInfoOrder.getMobile());//用户手机号
        order.setNickname(userInfoOrder.getNickname()); //用户昵称
        order.setStatus(0); //支付状态(0:未支付 1:已支付)
        order.setPayType(1); //支付类型(1:微信 2:支付宝)

        baseMapper.insert(order);
        //返回订单号
        return order.getOrderNo();
    }
}
