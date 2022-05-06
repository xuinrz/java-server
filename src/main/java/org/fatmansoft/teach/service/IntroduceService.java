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
        if (studentId == null) studentId = 1;
        Student s = studentRepository.findById(studentId).get();
        List<Practice> practiceList = practiceRepository.findPracticeListByStudentId(studentId);  //数据库查询操作
        Practice practice;
        List<Honor> honorList = honorRepository.findHonorListByStudentId(studentId);  //数据库查询操作
        Honor honor;
        List<Score> courseList = courseRepository.findByStudentIdInInf(studentId);  //数据库查询操作
        Score course;
        String name = s.getStudentName();
        List<Score> scoreList = stuInfRepository.findScoreByStudentName(name);
        Score score;
        double sum = 0, totleCredit = 0;
        for (int i = 0; i < scoreList.size(); i++) {

            score = scoreList.get(i);

            Integer credit = score.getCourse().getCredit();
            if (credit == null) continue;
            if (score.getGradePoint() == null) continue;
            totleCredit += credit;

            sum += credit * score.getGradePoint();
        }
        double x = sum / totleCredit;
        if (totleCredit == 0) x = 0;


        Map data = new HashMap();


        String content = "<!DOCTYPE html>" +
                "<html>" +
                "  <head>" +
                "    <meta charset='utf-8' />" +
                "    <meta http-equiv='X-UA-Compatible' content='IE=edge' />" +
                "    <meta" +
                "      name='viewport'" +
                "      content='width=device-width,initial-scale=1.0, maximum-scale=1.0, user-scalable=0'" +
                "    />" +""+
                "    <title>简而易见</title>" +
//                "    <link" +
//                "      href='http://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css'" +
//                "      rel='stylesheet'" +
//                "    />" +
                "    <link rel='stylesheet' href='https://xuinrz.github.io/Swiper/pc_resume.css' />" +
//                "    <link rel='stylesheet' href='https://xuinrz.github.io/Swiper/mobile.css' />  " +
                "<script src='https://xuinrz.github.io/Swiper/echarts.min.js'></script>" +
                "  <script src='https://xuinrz.github.io/Swiper/echarts-wordcloud.js'></script>" +
                "  <style> body { font-family: 'SourceHanSansSC', 'Open Sans';}</style></head>" +
                "  <body>" +
                "    <div class='template-box'>" +
                "      <div class='wrap-left'>" +
                "        <!-- 头像 -->" +
                "        <div class='image-box'>" +
                "          <img src='https://xuinrz.github.io/Swiper/creeper.jpeg' width='100%' height='100%' />" +
                "        </div>" +
                "        <div class='borderColor'></div>" +
                "        <!-- 基本信息 -->" +
                "        <div class='introduce-box'>" +
                "          <div class='mobile oauth-display-box'>" +
                "            <p class='self-name'>" + s.getStudentName() + "</p>" +
                "            <p class='self-job'></p>" +
                "          </div>" +
                "          <div class='item-cell'>" +
                "            <p class='forward-job'>基本信息 Basic</p>" +
                "            <ul class='fa-ul info'>" +
                "              <li class='job'>" +
                "                <i class='fa-li fa fa-square'></i>院校 : 山东大学软件学院" +
                "              </li>" +
                "              <li class='job'>" +
                "                <i class='fa-li fa fa-square'></i>专业 : 软件工程" +
                "              </li>" +
                "              <li class='job'><i class='fa-li fa fa-square'></i>学历 : 本科</li>" +
                "              <li class='job'>" +
                "                <i class='fa-li fa fa-square'></i>性别 : " + s.getSex() + "" +
                "              </li>" +
                "              <li class='job'>" +
                "                <i class='fa-li fa fa-square'></i>年龄 : " + s.getAge() + "" +
                "              </li>" +
                "              <li class='job'>" +
                "                <i class='fa-li fa fa-square'></i>政治面貌 : " + (s.getFace() == null || s.getFace().equals("") ? "无" : s.getFace()) + "" +
                "              </li>" +
                "            </ul>" +
                "          </div>" +
                "          <div class='item-cell'>" +
                "            <p class='forward-job'>联系方式 Contact</p>" +
                "            <ul class='fa-ul info'>" +
                "              <li><i class='fa-li fa fa-phone'></i>" + (s.getPhone() == null || s.getPhone().equals("") ? "无" : s.getPhone()) + "</li>" +
                "              <li>" +
                "                <i class='fa-li fa fa-envelope-open'></i><a style='color:white' href='mailto:" + s.getEmail() + "'>" + (s.getEmail() == null || s.getEmail().equals("") ? "无" : s.getEmail()) + "</a>" +
                "              </li>" +
                "            </ul>" +
                "          </div>" +
                "          <div class='item-cell'>" +
                "            <p class='forward-job'>个人评价 Remark</p>" +
                "            <ul" +
                "              class='fa-ul info'" +
                "              style='margin-left:.3rem;margin-right: 1.1rem'" +
                "            >" +
                "              <li class='remark' style='margin-top: 1rem;'>" +
                "                <p>" + (s.getPortrait() == null || s.getPortrait().equals("") ? "无" : s.getPortrait()) + "</p>" +
                "              </li>" +
                "            </ul>" +
                "          </div>" +
                "        </div>" +
                "      </div>" +
                "      <div class='wrap-right'>" +
                "        <div class='oauth-box pc-display'>" +
                "          <p class='oauth-name color'>" + s.getStudentName() + "</p>" +
                "        </div>" +


                "        <!-- 个人简介 -->" +
                "        <div class='self-introduce'>" +
                "          <p>github:<a style='color:blue' href='https://github.com/xuinrz'>https://github.com/xuinrz</a></p>" +
                "          <p>blog:<a style='color:blue' href='https://xuinrz.github.io/'>https://xuinrz.github.io/</a></p>" +
                "          <p>email:<a color='blue' href='mailto:" + s.getEmail() + "'>" + (s.getEmail() == null || s.getEmail().equals("") ? "无" : s.getEmail()) + "</a></p>" +
                "        </div>" +
                "<br/>" +
                "<br/>" +
                "        <p class='wrap-title color' style='margin-bottom: 0;'>" +
                "          <i class='fa fa-bar-chart wrap-icon'></i" +
                "          >学习成绩  Study" +
                "        </p>" +
                "        <div class='exper-box'>" +
                "          <ul class='intro-skill-cell'>" +
                "            <li class='skill-li'>" +
                "全科平均绩点：" + (String.format("%.2f", x)) + "" +
                "            </li>" +

                "          </ul>" +
                "        </div>" +
                        "        <!-- 项目经验 -->" +
                        "        <p class='wrap-title color'>" +
                        "          <i class='fa fa-bar-chart wrap-icon'></i" +
                        "          >获奖记录  Honor" +
                        "        </p>" +
                        "        <div class='exper-box' style='margin-bottom: 0;'>" +
                        "          <ul class='time-vertical'>";

        for (int i = 0; i <honorList.size(); i++) {
            honor = honorList.get(i);
            content+="<li class='vertical-li-style'><b></b><span>"+(i+1)+"</span>" +
                    "<a>"+DateTimeTool.parseDateTime(honor.getHonorDate(), "yyyy年MM月dd日")+""+
                    "<p class='pc-display' style='float: right;margin-right: 1.2rem'>获" + honor.getHonorLevel() + "奖项：" + honor.getHonorName() + "</p></a></li>";
        }


        content +=
                "        </ul></div>" +
                        "        <!-- 项目经验 -->" +
                        "        <p class='wrap-title color'>" +
                        "          <i class='fa fa-bar-chart wrap-icon'></i" +
                        "          >创新实践  Practice" +
                        "        </p>" +
                        "        <div class='exper-box' style='margin-bottom: 0;'>" +
                        "          <ul class='time-vertical'>";

        for (int i = 0; i <practiceList.size(); i++) {
            practice = practiceList.get(i);
            content+="<li class='vertical-li-style'><b></b><span>"+(i+1)+"</span>" +
                    "<a>"+practice.getpType()+""+practice.getpName()+
                    "<p class='pc-display' style='float: right;margin-right: 1.2rem'>指导教师："+practice.getpTeacher()+"</p></a></li>";
        }
                        content+="          </ul>" +
                        "        </div>";


        content+="<p class='wrap-title color' style='margin-bottom: 0;'>" +
                "          <i class='fa fa-bar-chart wrap-icon'></i" +
                "          >专业课程  Courses" +
                "        </p>" +
                "        <div class='exper-box'>" +
                "          <ul class='intro-skill-cell'>";
        for (int i = 0; i < courseList.size(); i++) {
            course = courseList.get(i);
            content+="<li class='skill-li'>"+course.getCourse().getCourseName()+"</li>";
        }




                        content+="</ul></div></div>" +
                        "      <div class='right-wrapper pc-display'>" +
                        "        <p class='image-box'></p>" +
                        "        <p class='borderColor'></p>" +
                        "        <p class='border-purple'></p>" +
                        "      </div>" +
                        "    </div>" +
                        "  </body>" +
                        "</html>";

        data.put("html", content);
        return data;
    }

    public Map getIntroduceDataMap2(Integer studentId) {


        if (studentId == null) studentId = 1;
        Student s = studentRepository.findById(studentId).get();

        List<Practice> practiceList = practiceRepository.findPracticeListByStudentId(studentId);

        List<Honor> honorList = honorRepository.findHonorListByStudentId(studentId);

        List<Score> courseList = courseRepository.findByStudentIdInInf(studentId);

        Map data = new HashMap();
        data.put("age", s.getAge());
        data.put("sex", s.getSex());
        data.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(), "MM-dd "));
        data.put("face", s.getFace());
        data.put("school", "本科");
        data.put("phone", s.getPhone());
        data.put("email", s.getEmail());
        data.put("portrait", s.getPortrait());
        data.put("myName", s.getStudentName());
        data.put("practiceList", practiceList);
        data.put("honorList", honorList);
        data.put("courseList", courseList);

        String name = s.getStudentName();
        List<Score> scoreList = stuInfRepository.findScoreByStudentName(name);
        Score score;
        double sum = 0, totleCredit = 0;
        int absenceCount, awardCount;
        for (int i = 0; i < scoreList.size(); i++) {

            score = scoreList.get(i);

            Integer credit = score.getCourse().getCredit();
            if (credit == null) continue;
            if (score.getGradePoint() == null) continue;
            totleCredit += credit;

            sum += credit * score.getGradePoint();
        }
        double x = sum / totleCredit;
        if (totleCredit == 0) x = 0;
        data.put("gradePoint", (String.format("%.2f", x)));

        return data;
    }
}

