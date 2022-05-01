package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.*;
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
    private ScoreRepository courseRepository;
    @Autowired
    private StuInfRepository stuInfRepository;



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

        List<Score> courseList = courseRepository.findByStudentIdInInf(studentId);  //数据库查询操作
        Score course;
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

    public Map getIntroduceDataMap2(Integer studentId) {


        if(studentId==null)studentId=1;
        Student s = studentRepository.findById(studentId).get();

        List<Practice> practiceList = practiceRepository.findPracticeListByStudentId(studentId);

        List<Honor> honorList = honorRepository.findHonorListByStudentId(studentId);

        List<Score> courseList = courseRepository.findByStudentIdInInf(studentId);

        Map data = new HashMap();
        data.put("age", s.getAge()  );
        data.put("sex", s.getSex()  );
        data.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(), "MM-dd ")  );
        data.put("face", s.getFace()  );
        data.put("school", "本科" );
        data.put("phone", s.getPhone()  );
        data.put("email", s.getEmail()  );
        data.put("portrait", s.getPortrait()  );
        data.put("myName", s.getStudentName());
        data.put("practiceList",practiceList);
        data.put("honorList",honorList);
        data.put("courseList",courseList);

        String name=s.getStudentName();
        List<Score> scoreList=stuInfRepository.findScoreByStudentName(name);
        Score score;
        double sum=0,totleCredit=0;
        int absenceCount,awardCount;
        for(int i=0;i<scoreList.size();i++){

            score=scoreList.get(i);

            int credit=score.getCourse().getCredit();
            totleCredit+=credit;
            sum+=credit*score.getGradePoint();
        }
        double x=sum/totleCredit;
        data.put("gradePoint",x);

        return data;
    }
}

