package com.atguigu.demo.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {
    public static void main(String[] args) {

        //实现excel的写操作

        //1.设置写入的excel文件的路径和名称
        String filename = "C:\\Users\\user\\Desktop\\mxy01.xlsx";

        //2.调用easyexcel的方法实现写操作
        EasyExcel
                .write(filename, DemoData.class)//第一个参数是文件路径的名称，第二个参数是实体类的class
                .sheet("学生列表")
                .doWrite(getData());
    }

    //创建静态方法返回list集合
    private static List<DemoData> getData() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setSno(i);
            data.setSname("lucy" + i);
            if (i%2==0) {
                data.setSex("男");
            } else {
                data.setSex("女");
            }
            list.add(data);
        }
        return list;
    }

}
