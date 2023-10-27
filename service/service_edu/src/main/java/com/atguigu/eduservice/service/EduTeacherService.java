package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author Russell
 * @since 2023-06-13
 */
public interface EduTeacherService extends IService<EduTeacher> {

    List<EduTeacher> getHotTeachers();

    Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageTeacher);
}
