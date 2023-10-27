package com.atguigu.vod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {
    String uploadAlyVideo(MultipartFile file);

    void removeMoreAlyVideo(List<String> videoIdList);
}
