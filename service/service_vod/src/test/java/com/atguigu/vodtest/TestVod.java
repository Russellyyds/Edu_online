package com.atguigu.vodtest;

import com.aliyun.oss.common.utils.StringUtils;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestVod {

    @Test
    public void getPlayUrl() throws Exception {
        String accessKeyId = "LTAI5tGbgnYqUZy3Fzgiid5U";
        String accessKeySecret = "oD9jUaTbT90bkAX5vQrBVSLWYyeIYi";
        DefaultAcsClient client = InitObject.initVodClient(accessKeyId, accessKeySecret);

        GetPlayInfoResponse response = new GetPlayInfoResponse();
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        request.setVideoId("573f93d0227071ee80400675b3ed0102");
        response = client.getAcsResponse(request);
        List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
        //播放地址
        for (GetPlayInfoResponse.PlayInfo playInfo : playInfoList) {
            System.out.print("PlayInfo.PlayURL = " + playInfo.getPlayURL() + "\n");
        }
        System.out.print("VideoBase.Title = " + response.getVideoBase().getTitle() + "\n");

    }

    @Test
    public void getVideoPlayAuth() throws ClientException {

        String accessKeyId = "LTAI5tGbgnYqUZy3Fzgiid5U";
        String accessKeySecret = "oD9jUaTbT90bkAX5vQrBVSLWYyeIYi";
        DefaultAcsClient client = InitObject.initVodClient(accessKeyId, accessKeySecret);
        GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId("573f93d0227071ee80400675b3ed0102");

        response= client.getAcsResponse(request);
        System.out.println(response.getPlayAuth());

    }

    @Test
    public void test1(){
        List<String> list1=new ArrayList<>(Arrays.asList("1","2","3","4"));
        System.out.println(StringUtils.join(Arrays.toString(list1.toArray())));
    }
}