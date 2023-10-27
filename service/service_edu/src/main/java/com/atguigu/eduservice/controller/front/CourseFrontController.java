package com.atguigu.eduservice.controller.front;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.ordervo.CourseWebVoOrder;
import com.atguigu.eduservice.client.OrdersClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.chapter.ChapterVo;
import com.atguigu.eduservice.entity.frontvo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontvo.CourseWebVo;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping("/eduservice/coursefront")
@RestController
public class CourseFrontController {
    @Autowired
    private EduCourseService courseService;
    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private OrdersClient ordersClient;

    @PostMapping("getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable long page,
                                @PathVariable long limit,
                                @RequestBody(required = false) CourseFrontVo courseFrontVo) {
        Page<EduCourse> pageCourse = new Page<>(page, limit);
        Map<String, Object> map = courseService.getCourseFrontList(pageCourse, courseFrontVo);
        return R.ok().data(map);
    }

    //2.查询课程详情
    @GetMapping("getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request) {
        //根据课程id查询课程信息(手写sql语句来实现)
        CourseWebVo courseWebVo = courseService.getBaseCourseInfo(courseId);

        //根据课程id查询课程章节和小节
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);

        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        boolean buyCourse = ordersClient.isBuyCourse(courseId,memberId);
        return R.ok().data("courseWebVo", courseWebVo).data("chapterVideoList", chapterVideoList).data("isBuy",buyCourse);
    }

    //根据课程id获取课程信息(该方法供远程调用使用)
    @PostMapping("getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable("id") String id) {
        CourseWebVo courseInfo = courseService.getBaseCourseInfo(id);
        //把courseInfo对象里面的值复制给courseWebVoOrder对象
        CourseWebVoOrder courseWebVoOrder = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfo, courseWebVoOrder);

        return courseWebVoOrder;
    }


}
