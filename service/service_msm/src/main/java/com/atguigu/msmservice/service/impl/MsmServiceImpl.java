package com.atguigu.msmservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.msmservice.service.MsmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;


@Service
public class MsmServiceImpl implements MsmService {


    @Override
    public boolean send(HashMap<String, Object> param, String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        DefaultProfile profile=
                DefaultProfile.getProfile("default","LTAI5tGbgnYqUZy3Fzgiid5U"
                        ,"oD9jUaTbT90bkAX5vQrBVSLWYyeIYi");
        IAcsClient client=new DefaultAcsClient(profile);

        //设置相关参数(固定的,不需要修改)
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST); //提交方式
        request.setDomain("dysmsapi.aliyuncs.com"); //发送时要访问阿里云中的哪个地方
        request.setVersion("2017-05-25"); //版本号
        request.setAction("SendSms"); //请求里面的哪个方法

        request.putQueryParameter("PhoneNumbers", phone); //设置要发送的手机号
        request.putQueryParameter("SignName", "我的AI学院在线教育网站"); //在阿里云申请的签名名称
        request.putQueryParameter("TemplateCode", "SMS_282605312"); //在阿里云中申请的模板Code
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param)); //验证码数据



        try {
            //最终发送
            CommonResponse response = client.getCommonResponse(request);

            boolean success = response.getHttpResponse().isSuccess();
            return success;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
