package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduTeacherService;
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
 * 讲师 前端控制器
 * </p>
 *
 * @author Russell
 * @since 2023-06-13
 */
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/edu-teacher")
@CrossOrigin
public class EduTeacherController {
    @Autowired
    private EduTeacherService eduTeacherService;

    @ApiOperation(value = "所有讲师列表")
    //restful 风格
    @GetMapping("/findAll")
    public R findAllTeacher() {
        //调用service的方法实现查询所有的操作
        List<EduTeacher> list1 = eduTeacherService.list(null);
        System.out.println(list1);
        return R.ok().data("items", list1);
    }

    @ApiOperation(value = "根据ID逻辑删除讲师")
    @DeleteMapping("/{id}")
    public R removeById(
            @ApiParam(name = "id", value = "讲师ID", required = true)
            @PathVariable("id") String id) {
        boolean b = eduTeacherService.removeById(id);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }

    }


    @ApiOperation(value = "分页讲师列表")
    @GetMapping("pageTeacher/{current}/{limit}")//current表示当前页，limit表示每页记录数
    public R pageListTeacher(
            @ApiParam(name = "current", value = "当前页码", required = true)
            @PathVariable long current,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable long limit) {

        //创建page对象
        Page<EduTeacher> pageTeacher = new Page<>(current, limit);

        //调用方法实现分页
        eduTeacherService.page(pageTeacher, null);

        long total = pageTeacher.getTotal();//总记录数
        List<EduTeacher> records = pageTeacher.getRecords();//该页数据的list集合

        return R.ok().data("total", total).data("rows", records);
    }

    //条件查询带分页

    @ApiOperation(value = "条件分页讲师列表")
    @PostMapping("pageTeacherCondition/{current}/{limit}")
    public R pageTeacherCondition(@ApiParam(name = "current", value = "当前页码", required = true)
                                  @PathVariable long current,
                                  @ApiParam(name = "limit", value = "每页记录数", required = true)
                                  @PathVariable long limit,
                                  @RequestBody(required = false) TeacherQuery teacherQuery) {
        Page<EduTeacher> eduTeacherPage = new Page<>(current, limit);
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        //判断条件值是否为空,如果不为空就拼接该条件值
        if (!StringUtils.isEmpty(name)) {
            //构建条件
            //模糊查询
            wrapper.like("name", name);
        }
        if (!StringUtils.isEmpty(level)) {
            //构建条件
            wrapper.eq("level", level);
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
        eduTeacherService.page(eduTeacherPage, wrapper);
        long total = eduTeacherPage.getTotal();//总记录数
        List<EduTeacher> records = eduTeacherPage.getRecords();//该页数据的list集合
        return R.ok().data("total", total).data("rows", records);
    }

    @PostMapping("/addTeacher")
    @ApiOperation(value = "添加新讲师")
    public R addTeacher(@ApiParam(name = "eduTeacher", value = "讲师对象", required = true)
                        @RequestBody(required = true) EduTeacher eduTeacher) {
        boolean save = eduTeacherService.save(eduTeacher);
        if (save) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping("/getTeacher/{id}")
    public R getTeacher(@ApiParam(name = "id", value = "讲师ID", required = true)
                      @PathVariable("id") String id) {
        return R.ok().data("teacher", eduTeacherService.getById(id));
    }

    //6.根据含有id的讲师对象修改讲师
    @ApiOperation(value = "根据含有id的讲师对象修改讲师")
    @PostMapping("/updateTeacher")
    public R updateTeacher(
            @ApiParam(name = "eduTeacher", value = "含有id的讲师对象", required = true)
            @RequestBody EduTeacher eduTeacher) {
        boolean flag = eduTeacherService.updateById(eduTeacher);
        if (flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

}

