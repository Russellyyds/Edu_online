package com.atguigu.eduservice.front;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/eduservice/indexfront")
@CrossOrigin
public class IndexFrontController {
    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduTeacherService teacherService;


    @ApiOperation(value = "//查询前8条热门课程,查询前4条名师")
    @GetMapping("index")
    public R index() {
        //查询前8条热门课程


        List<EduCourse> eduList= courseService.getHotCourses();

        //查询前4条名师


        List<EduTeacher> teacherList = teacherService.getHotTeachers();


        return R.ok().data("eduList",eduList).data("teacherList",teacherList);
    }
}
