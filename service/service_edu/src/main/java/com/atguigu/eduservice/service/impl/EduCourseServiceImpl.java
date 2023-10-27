package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.frontvo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontvo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Russell
 * @since 2023-06-25
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {
    @Autowired
    private EduCourseDescriptionService courseDescriptionService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private EduVideoService eduVideoService;
    @Autowired
    private EduCourseDescriptionService eduCourseDescriptionService;


    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        //1.向课程表添加课程基本信息
        //①将CourseInfoVo对象转换为EduCourse对象
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int insert = baseMapper.insert(eduCourse);

        //③判断是否插入成功
        if (insert <= 0) {
            throw new GuliException(20001, "插入失败");
        }

        String cid = eduCourse.getId();

        //2.向课程简介表添加课程简介
        //①将CourseInfoVo对象转换为courseDescription对象
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
        courseDescription.setId(cid);
        courseDescriptionService.save(courseDescription);
        //②设置课程简介id和刚刚插入的课程id相同,这样才可以一对一
        //③执行课程简介模块的业务层方法向课程简介表插入数据
        return cid;

    }

    @Override
    public CourseInfoVo getCourseInfo(String courseId) {

        EduCourse eduCourse = baseMapper.selectById(courseId);
        EduCourseDescription eduDescription = courseDescriptionService.getById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse, courseInfoVo);
        courseInfoVo.setDescription(eduDescription.getDescription());
        return courseInfoVo;
    }

    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int i = baseMapper.updateById(eduCourse);
        if (i <= 0) {
            throw new GuliException(20001, "修改课程信息失败");
        }
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoVo.getId());
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        boolean b = courseDescriptionService.updateById(eduCourseDescription);

    }

    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);


        return publishCourseInfo;
    }

    @Override
    public void removeCourse(String courseId) {
        //1.根据课程id删除小节
        eduVideoService.removeVideoByCourseId(courseId);

        //2.根据课程id删除章节
        chapterService.removeChapterByCourseId(courseId);

        //3.根据课程id删除课程描述
        courseDescriptionService.removeById(courseId);

        int i = baseMapper.deleteById(courseId);
        if (i == 0) {
            throw new GuliException(20001, "删除失败");
        }

    }

    @Cacheable(value = "courses",key = "'getHotCourses'")
    @Override
    public List<EduCourse> getHotCourses() {
        QueryWrapper<EduCourse> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.orderByDesc("id");
        courseQueryWrapper.last("limit 8");
        List<EduCourse> eduList  = this.list(courseQueryWrapper);
        return eduList;
    }

    //课程条件查询带分页
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //判断条件值是否为空,不为空就拼接条件
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())) { //一级分类
            wrapper.eq("subject_parent_id", courseFrontVo.getSubjectParentId());
        }
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectId())) { //二级分类
            wrapper.eq("subject_id", courseFrontVo.getSubjectId());
        }
        if (!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())) { //关注度排序
            wrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())) { //最新时间排序
            wrapper.orderByDesc("gmt_create");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())) { //价格排序
            wrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageParam, wrapper);
        //获取分页所有数据
        List<EduCourse> records = pageParam.getRecords(); //该页数据的list集合
        long current = pageParam.getCurrent(); //当前页
        long pages = pageParam.getPages(); //总页数
        long size = pageParam.getSize(); //每页记录数
        long total = pageParam.getTotal(); //总记录数
        boolean hasNext = pageParam.hasNext(); //是否有下一页
        boolean hasPrevious = pageParam.hasPrevious(); //是否有上一页

        //把分页数据放到map集合中
        HashMap<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    //根据课程id查询课程信息(手写sql语句来实现)
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }


}




