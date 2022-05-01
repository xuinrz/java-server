package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.CourseManagement;
import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Practice;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntroduceService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PracticeRepository practiceRepository;
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private CourseManagementRepository courseRepository;

    //个人简历信息数据准备方法  请同学修改这个方法，请根据自己的数据的希望展示的内容拼接成字符串，放在Map对象里， attachList 可以方多段内容，具体内容有个人决定
    public Map getIntroduceDataMap(Integer studentId) {
        if(studentId==null)studentId=1;
        Student s = studentRepository.findById(studentId).get();
        String practiceContent = "";

        List<Practice> practiceList = practiceRepository.findPracticeListByStudentId(studentId);  //数据库查询操作
        Practice practice;
        if (practiceList == null || practiceList.size() == 0)
            practiceContent = "无";
        else {
            for (int i = 0; i < practiceList.size(); i++) {
                practice = practiceList.get(i);
                practiceContent += practice.getpName() + practice.getpType() + " (指导老师:" + practice.getpTeacher() + ")" + "；  ";
            }
        }

        String honorContent = "";

        List<Honor> honorList = honorRepository.findHonorListByStudentId(studentId);  //数据库查询操作
        Honor honor;
        if (honorList == null || honorList.size() == 0)
            honorContent = "无";
        else {
            for (int i = 0; i < honorList.size(); i++) {
                honor = honorList.get(i);
                honorContent += DateTimeTool.parseDateTime(honor.getHonorDate(), "yyyy年MM月dd日 ") + "获" + honor.getHonorLevel() + "奖项：" + honor.getHonorName() + "； ";
            }
        }

        String courseContent = "";

        List<CourseManagement> courseList = courseRepository.findByStudentIdInInf(studentId);  //数据库查询操作
        CourseManagement course;
        if (courseList == null || courseList.size() == 0)
            courseContent = "无";
        else {
            for (int i = 0; i < courseList.size(); i++) {
                course = courseList.get(i);
                if (i != courseList.size() - 1)
                    courseContent += course.getCourse().getCourseName() + "、 ";
                else
                    courseContent += course.getCourse().getCourseName() + "  ";
            }
        }
            String basicContent="性别："+s.getSex()+" | "+" 年龄："+s.getAge()+" | " + "出生日期" +
                    "："+DateTimeTool.parseDateTime(s.getBirthday(), "yyyy年MM月dd日 ")+" | "+"政治面貌："+s.getFace()+"|"+
                    " 联系电话："+s.getPhone()+" | " +"电子邮箱："+s.getEmail();


            Map data = new HashMap();
            data.put("myName", s.getStudentName());   // 学生信息
            //data.put("overview", "本人.是xzx他爸");  //学生基本信息综述
            List attachList = new ArrayList();
            Map m;
            m = new HashMap();
            m.put("title", "个人简述");   //
            m.put("content", s.getPortrait());  // 学生成绩综述
            attachList.add(m);
            m = new HashMap();
            m.put("title", "基本信息");   //
            m.put("content", basicContent);  // 学生成绩综述
            attachList.add(m);
            m = new HashMap();
            m.put("title", "学习成绩");   //
            m.put("content", "成绩...4.5");  // 学生成绩综述
            attachList.add(m);
            m = new HashMap();
            m.put("title", "创新实践");
            m.put("content", practiceContent);  // 社会实践综述
            attachList.add(m);
            m = new HashMap();
            m.put("title", "获奖经历");
            m.put("content", honorContent);  // 社会实践综述
            attachList.add(m);
            m = new HashMap();
            m.put("title", "专业课程");
            m.put("content", courseContent);  // 社会实践综述
            attachList.add(m);

            data.put("attachList", attachList);
            return data;
        }
}

