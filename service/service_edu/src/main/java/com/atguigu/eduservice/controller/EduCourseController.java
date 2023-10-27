package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author Russell
 * @since 2023-06-25
 */
@Api(description = "关于课程的一些操作")
@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "添加课程基本信息")
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo) {
        //需要添加之后返回课程id，为了后面添加课程大纲
        String id = courseService.saveCourseInfo(courseInfoVo);
        return R.ok().data("courseId", id);
    }

    @ApiOperation(value = "根据课程id查询课程基本信息")
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId) {
        CourseInfoVo courseInfoVo = courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo", courseInfoVo);
    }

    //修改课程基本信息
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo) {
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    @GetMapping("getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id) {
        CoursePublishVo coursePublishVo = courseService.publishCourseInfo(id);
        return R.ok().data("publishCourse", coursePublishVo);
    }

    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id) {
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);
        return R.ok();

    }

//    @GetMapping
//    public R getCourseList() {
//        List<EduCourse> list = courseService.list(null);
//        return R.ok().data("list", list);
//    }


    @ApiOperation(value = "条件分页课程列表")
    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R pageTeacherCondition(@ApiParam(name = "current", value = "当前页码", required = true)
                                  @PathVariable long current,
                                  @ApiParam(name = "limit", value = "每页记录数", required = true)
                                  @PathVariable long limit,
                                  @RequestBody(required = false) CourseQuery courseQuery) {
        Page<EduCourse> eduCoursePage = new Page<>(current, limit);
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        String title = courseQuery.getTitle();
        Integer status = courseQuery.getStatus();
        String begin = courseQuery.getBegin();
        String end = courseQuery.getEnd();
        //判断条件值是否为空,如果不为空就拼接该条件值
        if (!StringUtils.isEmpty(title)) {
            //构建条件
            //模糊查询
            wrapper.like("title", title);
        }
        if (!StringUtils.isEmpty(status)) {
            //构建条件
            if (status==1){
                wrapper.eq("status", "Normal");
            }
            if(status==0){
                wrapper.eq("status","Draft");}

        }
        if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapper.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapper.le("gmt_create", end);
        }
        //排序
        //使用创建时间做降序排序
        wrapper.orderByDesc("gmt_create");
        //调用方法实现条件查询分页
        courseService.page(eduCoursePage, wrapper);
        long total = eduCoursePage.getTotal();//总记录数
        System.out.println("总记录数：" + total);
        List<EduCourse> records = eduCoursePage.getRecords();//该页数据的list集合
        return R.ok().data("total", total).data("rows", records);
    }


    @DeleteMapping("{courseId}")

    public R deleteCourse(@PathVariable String courseId){
        courseService.removeCourse(courseId);
        return R.ok();
    }

}

