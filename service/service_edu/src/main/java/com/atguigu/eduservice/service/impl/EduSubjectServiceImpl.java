package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.entity.subject.TwoSubject;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Russell
 * @since 2023-06-24
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
//1.得到文件输入流
            InputStream in = file.getInputStream();
            //2.调用方法进行读取
            EasyExcel.read(in,
                    SubjectData.class,
                    new SubjectExcelListener(subjectService))
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //1.查询所有一级分类(parentid = 0)

        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id", "0");
        List<EduSubject> oneSubjects = baseMapper.selectList(wrapperOne);
        //List<EduSubject> oneSubjectList = this.list(wrapperOne);
        //2.查询所有二级分类(parentid != 0)
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id", "0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);

        //3.创建一个list集合,用于存储最终封装的数据
        List<OneSubject> finalSubjectList = new ArrayList<>();
//4.封装一级分类
        /**
         * 在第1步查询出来的所有一级分类是List<EduSubject>类型的,我们需
         * 要想办法将List<EduSubject>类型转换为List<OneSubject>类型
         */
        //遍历oneSubjectList集合

        for (int i = 0; i < oneSubjects.size(); i++) {
            //①得到oneSubjectList中的每个EduSubject对象
            EduSubject eduSubject = oneSubjects.get(i);
            //②把eduSubject中我们需要的值获取出来,放到oneSubject中
            OneSubject oneSubject = new OneSubject();
            //oneSubject.setId(eduSubject.getId());
            //oneSubject.setTitle(eduSubject.getTitle());
            /**
             * 上面这两个代码我们可以用工具类来实现
             * 注意:工具类BeanUtils是Spring包里面的,别导错包
             */
            BeanUtils.copyProperties(eduSubject,oneSubject);
            //③将每个oneSubject放到finalSubjectList中
            finalSubjectList.add(oneSubject);

            //④封装二级分类
            //创建list集合封装每个一级分类下的所有二级分类
            List<TwoSubject> twoFinalSubjectList = new ArrayList<>();
            //遍历twoSubjectList集合
            for (int m = 0; m < twoSubjectList.size(); m++) {
                //④.1:得到twoSubjectList中的每个EduSubject对象
                EduSubject tSubject = twoSubjectList.get(m);
                //若二级分类的parent_id和一级分类的id相等,那么就进行封装
                if (tSubject.getParentId().equals(eduSubject.getId())){
                    //④.2:把tSubject中我们需要的值获取出来,放到twoSubject中
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(tSubject,twoSubject);
                    //④.3:将每个twoSubject放到twoFinalSubjectList中
                    twoFinalSubjectList.add(twoSubject);
                }

            }
            oneSubject.setChildren(twoFinalSubjectList);

        }

        return finalSubjectList;
    }
}
