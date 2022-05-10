package org.fatmansoft.teach.controllers;


import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.HomeworkRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class HomeworkController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    public List getHomeworkMapList(Integer id) {
        List dataList = new ArrayList();
        List<Homework> sList = homeworkRepository.findByCourseId(id);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Homework sc;
        Student s;
        Course c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            if("是".equals(sc.getSubmission())) {    //数据库存的是编码，显示是名称
                m.put("submission","是");}
            else{
                m.put("submission","否");
            }
            m.put("deadline",DateTimeTool.parseDateTime(sc.getDeadline(),"yyyy-MM-dd"));
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/homeworkInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse homeworkInit(@Valid @RequestBody DataRequest dataRequest) {

        Integer id = dataRequest.getInteger("courseId");
        if (id==null)id=0;
        List dataList = getHomeworkMapList(id);

        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/homeworkDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse homeworkDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Homework s= null;
        Optional<Homework> op;
        if(id != null) {
            op= homeworkRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            homeworkRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/homeworkEditInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse homeworkEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Homework sc= null;
        Student s;
        Course c;
        Optional<Homework> op;
        if(id != null) {
            op= homeworkRepository.findById(id);
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for(i = 0; i <cList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("label",c.getCourseName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        form.put("courseId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("courseId",sc.getCourse().getId());
            form.put("submission",sc.getSubmission());
            form.put("deadline",sc.getDeadline());
        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewHomeworkId(){
        Integer  id = homeworkRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }
    @PostMapping("/homeworkEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse homeworkEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        String submission = CommonMethod.getString(form,"submission");
        Date deadline = CommonMethod.getDate(form,"deadline");
        Homework sc= null;
        Student s= null;
        Course c = null;
        Optional<Homework> op;
        if(id != null) {
            op= homeworkRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            Boolean isChosen=(scoreRepository.isCourseManagementExist(studentId,courseId).size()!=0);
            if(!isChosen) return CommonMethod.getReturnMessageError("该学生未选择该课程");
            sc = new Homework();   //不存在 创建实体对象
            id = getNewHomeworkId(); //获取新的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        Boolean isChosen=(scoreRepository.isCourseManagementExist(studentId,courseId).size()!=0);
        if(!isChosen) return CommonMethod.getReturnMessageError("该学生未选择该课程");
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setSubmission(submission);
        sc.setDeadline(deadline);
        homeworkRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }


}
