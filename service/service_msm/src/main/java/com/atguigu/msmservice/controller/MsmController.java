package com.atguigu.msmservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.msmservice.service.MsmService;
import com.atguigu.msmservice.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RequestMapping("/edumsm/msm")
@RestController
@CrossOrigin
public class MsmController {

    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("send/{phone}")
    public R sendMsm(@PathVariable("phone") String phone) {

        //1.从redis中获取验证码,如果能取到就不需要使用阿里云发送验证码,我们直接返回
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return R.ok();
        }
        //2.如果不能从redis中获取到,就使用阿里云发送验证码
        code = RandomUtil.getFourBitRandom();
        HashMap<String, Object> param = new HashMap<>();
        param.put("code", code);

        System.out.println("param"+param);
        boolean isSend = msmService.send(param, phone);
        if (isSend) {
            //发送成功,把发送成功的验证码放到redis里面并且设置有效时长
            redisTemplate.opsForValue().set(phone, code,5, TimeUnit.MINUTES);
            return R.ok();
        } else {
            return R.error().message("短消息发送失败");
        }

    }
}
