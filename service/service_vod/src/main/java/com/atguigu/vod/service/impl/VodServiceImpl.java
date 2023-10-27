package com.atguigu.vod.service.impl;

import org.apache.commons.lang.StringUtils;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.vod.service.VodService;
import com.atguigu.vod.utils.ConstantVodUtils;
import com.atguigu.vod.utils.InitVodClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Service
public class VodServiceImpl implements VodService {


    @Override
    public String uploadAlyVideo(MultipartFile file) {
        try {
            //上传的文件的原始名称(如:01.mp4)
            String fileName = file.getOriginalFilename();
            //上传后在阿里云显示的名称(带不带后缀都行,我这里没带)
            assert fileName != null;
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();


            UploadStreamRequest request = new UploadStreamRequest(
                    ConstantVodUtils.ACCESS_KEY_ID,
                    ConstantVodUtils.ACCESS_KEY_SECRET,
                    title,
                    fileName,
                    inputStream);
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            String videoId = null;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
            }

            return videoId;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }




    //根据多个视频id删除阿里云中的视频
    @Override
    public void removeMoreAlyVideo(List videoIdList) {
        try {
            //1.创建初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(
                    ConstantVodUtils.ACCESS_KEY_ID,
                    ConstantVodUtils.ACCESS_KEY_SECRET);

            //2.创建删除的request
            DeleteVideoRequest request = new DeleteVideoRequest();

            //3.向request对象里面设置视频id
            //①需要先将videoIdList集合中的id遍历为1,2,3的形式
            String videoIds = StringUtils.join(videoIdList.toArray(), ",");
            //②设置视频id
            request.setVideoIds(videoIds);

            //4.调用初始化对象里面的方法,实现删除
            client.getAcsResponse(request);
        } catch(Exception e) {
            e.printStackTrace();
            throw new GuliException(20001, "删除视频失败");
        }
    }
}
