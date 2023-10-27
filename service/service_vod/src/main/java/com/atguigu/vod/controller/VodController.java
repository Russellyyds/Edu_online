package com.atguigu.vod.controller;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.vod.service.VodService;
import com.atguigu.vod.utils.ConstantVodUtils;
import com.atguigu.vod.utils.InitVodClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/eduvod/video")
@CrossOrigin
public class VodController {
    @Autowired
    private VodService vodService;
    @PostMapping("/uploadAlyVideo")
    public R uploadAlyVideo(MultipartFile file){
        String videoId=vodService.uploadAlyVideo(file);
        return R.ok().data("videoId", videoId);
    }

    @DeleteMapping("removeAlyVideo/{id}")
    public R removeAlyVideo(@PathVariable String id){
        try {
            //1.创建初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(
                    ConstantVodUtils.ACCESS_KEY_ID,
                    ConstantVodUtils.ACCESS_KEY_SECRET);
            //2.创建删除的request
            DeleteVideoRequest request = new DeleteVideoRequest();

            //3.向request对象里面设置视频id
            request.setVideoIds(id);

            //4.调用初始化对象里面的方法,实现删除
            client.getAcsResponse(request);

            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001,"删除视频失败");
        }
    }

    //根据多个视频id删除阿里云中的视频
    @DeleteMapping("delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList) {
        vodService.removeMoreAlyVideo(videoIdList);
        return R.ok();
    }

    //根据视频id获取视频凭证
    @GetMapping("getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id) {
        try {
            //1.创建初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(
                    ConstantVodUtils.ACCESS_KEY_ID,
                    ConstantVodUtils.ACCESS_KEY_SECRET);

            //2.创建获取视频凭证的request和response
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();

            //3.向request对象里面设置视频id(加密视频id、没有加密视频的id都是可以的)
            request.setVideoId(id);

            //4.调用初始化对象里面的方法,获取视频信息
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);

            //5.从获取到的视频信息中取视频凭证的信息
            String playAuth = response.getPlayAuth();

            return R.ok().data("playAuth", playAuth);
        } catch (Exception e) {
            throw new GuliException(20001, "获取视频凭证失败");
        }
    }


}
