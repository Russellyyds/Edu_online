package com.atguigu.educenter.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.educenter.utils.ConstantWxUtils;
import com.atguigu.educenter.utils.HttpClientUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.util.HashMap;

@Controller
@RequestMapping("/api/ucenter/wx")
@CrossOrigin
public class WxApiController {
    @Autowired
    private UcenterMemberService memberService;

    @GetMapping("callback")
    public String callback(String code, String state) {
        try {
            //1.拿着code(这是一个临时票据,类似于验证码)请求微信给的固定地址
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";
            //1.1给地址拼接三个参数:id、秘钥、code值
            String accessTokenUrl = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code);
            //1.2使用httpclient请求拼接好的地址,得到openid和access_token
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            //输出accessTokenInfo看一下这个字符串长什么样,以便分析后面的业务逻辑
            System.out.println(accessTokenInfo);
            //从json字符串中获取openid和access_token
//使用json工具把accessTokenInfo字符串转换为map集合,根据map中的key获取对应的值
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String access_token = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");
            UcenterMember member = memberService.getOpenIdMember(openid);

            if(member==null) {
//2.拿着access_token和openid请求微信给的固定地址
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
//2.1给地址拼接两个参数:access_token、openid
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
//2.2使用httpclient请求拼接好的地址
                String userInfo = HttpClientUtils.get(userInfoUrl);
//输出userInfo看一下这个字符串长什么样,以便分析后面的业务逻辑
                System.out.println(userInfo);

                //3.获取扫码人信息
//3.1将json字符串转换为map集合
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname"); //用户昵称
                String headimgurl = (String) userInfoMap.get("headimgurl"); //用户头像

                //4.微信扫码登录时注册的过程不再由用户完成,而是由我们后端直接实现
//所以我们此时需要将用户的openid、昵称、头像加到数据库中

//根据opendi判断数据库中是否已有该用户
                if (member == null) { //数据表中没有这个openid,进行添加
                    member = new UcenterMember();
                    member.setOpenid(openid);
                    member.setNickname(nickname);
                    member.setAvatar(headimgurl);
                    memberService.save(member);
                }

            }
            //使用jwt根据member对象(对象中有用户信息)生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
            //返回到首页面
            return "redirect:http://localhost:3000?token=" + jwtToken;
        } catch (Exception e) {
            throw new GuliException(20001, "登录失败");
        }
    }

    @GetMapping("login")
    public String getWxCode() {
        //1.url中的%s就相当于问号(?),代表占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //2.对redirect_url进行URLEncode编码
        String redirect_url = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            redirect_url = URLEncoder.encode(redirect_url, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.给baseUrl中的占位符(%s)赋值
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_REDIRECT_URL,
                "atguigu" //老师说了state目前没啥用,只是给他传个值atguigu
        ); //方法的返回值就是完整的带有参数的url地址
        //4.重定向到请求微信地址里面
        return "redirect:" + url;
    }
}
