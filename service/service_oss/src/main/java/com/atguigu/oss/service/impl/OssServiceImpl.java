package com.atguigu.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.oss.service.OssService;
import com.atguigu.oss.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Override
    public String uploadFileAvatar(MultipartFile file) {
//1.工具类获取数据
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        //2.创建OSS实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            //3.获取到将要上传的文件的输入流
            InputStream inputStream = file.getInputStream();

            //4.获取到将要上传的文件的名称
            String fileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + fileName;
            //完善:把文件按照日期进行分类:2022/08/17/01.jpg
//1.获取当前时间
            String datePath = new DateTime().toString("yyyy/MM/dd");
//2.拼接
            fileName = datePath + "/" + fileName;


            //5.调用oss的方法实现上传
            ossClient.putObject(bucketName, fileName, inputStream);

            //6.关闭OSS对象
            ossClient.shutdown();

            //7.拼接路径
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
