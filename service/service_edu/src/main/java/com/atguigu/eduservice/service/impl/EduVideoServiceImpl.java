package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.mapper.EduVideoMapper;
import com.atguigu.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author Russell
 * @since 2023-06-25
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {


    @Autowired
    private VodClient vodClient;

    @Override
    public void removeVideoByCourseId(String courseId) {
//        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
//        wrapper.eq("course_id", courseId);
//        wrapper.select("video_source_id");
//        List<EduVideo> eduVideoLists = this.baseMapper.selectList(wrapper);
//        List<String> strings = eduVideoLists.stream().filter(eduVideo -> eduVideo.getVideoSourceId() != null)
//                .map(EduVideo::getVideoSourceId).collect(Collectors.toList());
//
//
////        把该课程所有的视频都查出来
//        if (strings.size()>0) {
//            vodClient.deleteBatch(strings);
//        }


        //TODO 删除小节前需要先删除小节下的视频文件
//1.根据课程id查询课程中所有的视频id(视频id在小节表中,所以我们查小节表edu_video)
//①得到List<EduVideo>
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id", courseId);
        wrapperVideo.select("video_source_id"); //查询指定的列
        List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);
//②将List<EduVideo>变为List<String>
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < eduVideoList.size(); i++) {
            EduVideo eduVideo = eduVideoList.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();
            //将不为空的视频id放到videoIds集合中
            if (!StringUtils.isEmpty(videoSourceId)) {
                videoIds.add(videoSourceId);
            }
        }
//2.远程调用:根据多个视频id删除阿里云中的视频
//判断:如果课程下没有一个视频,那就不用调用这个方法了
        if (videoIds.size() > 0) {
            vodClient.deleteBatch(videoIds);
        }



        baseMapper.delete(new QueryWrapper<EduVideo>().eq("course_id", courseId));
    }
}
