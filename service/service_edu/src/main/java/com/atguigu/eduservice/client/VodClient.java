package com.atguigu.eduservice.client;


import com.atguigu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


//@FeignClient注解用于指定从哪个服务中调用功能 ,名称与被调用的服务名保持一致
//@PathVariable注解一定要指定参数名称,否则出错
@FeignClient(name = "service-vod",fallback =VodFileDegradeFeignClient.class)
@Component
public interface VodClient {

    @DeleteMapping("/eduvod/video/removeAlyVideo/{id}")
    public R removeAlyVideo(@PathVariable("id") String id);


    @DeleteMapping("/eduvod/video/delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList);
}
