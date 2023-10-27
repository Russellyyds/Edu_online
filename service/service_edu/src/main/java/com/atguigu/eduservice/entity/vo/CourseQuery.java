package com.atguigu.eduservice.entity.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;




@ApiModel(value = "Course查询对象", description = "课程查询对象封装")
@Data
public class CourseQuery {
    private String title;
    private Integer status;
    private String begin;
    private String end;
}
