package org.fatmansoft.teach.controllers;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.CourseManagementRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.CourseManagementRepository;

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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class CourseManagementController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseManagementRepository courseManagementRepository;


    public List getCourseManagementMapList(String numName,String courseName) {
        List dataList = new ArrayList();
        List<CourseManagement> sList = courseManagementRepository.findByNumNameCourseName(numName,courseName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        CourseManagement cm;
        Course c;
        Student s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            cm = sList.get(i);
            s = cm.getStudent();
            c = cm.getCourse();
            m = new HashMap();
            m.put("id", cm.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNumber",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("credit",c.getCredit());
            m.put("courseHour",c.getHours());
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/courseManagementInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseManagementInit(@Valid @RequestBody DataRequest dataRequest) {
        String studentNum = dataRequest.getString("studentNum");
        String courseName = dataRequest.getString("courseName");
        if (studentNum==null) studentNum="";
        if (courseName==null) courseName="";
        List dataList = getCourseManagementMapList(studentNum,courseName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getCourseManagementMapListForQuery(String numName,String courseName) {
        List dataList = new ArrayList();
        List<CourseManagement> courseManagementList = courseManagementRepository.findByNumNameCourseName(numName,courseName);  //数据库查询操作
        if (courseManagementList == null || courseManagementList.size() == 0)
            return dataList;
        Student s;
        CourseManagement cm;
        Course c;
        Map m;
        for(int i = 0; i < courseManagementList.size();i++) {
            cm = courseManagementList.get(i);
            s = cm.getStudent();
            c = cm.getCourse();
            m = new HashMap();
            m.put("id", cm.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNumber", c.getCourseNum());
            m.put("courseName", c.getCourseName());
            m.put("credit", c.getCredit());
            m.put("courseHour", c.getHours());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/courseManagementQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseManagementQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String type = dataRequest.getString("courseName");
        if(numName==null)
            numName="";
        if(type ==null)
            type= "";
        List dataList = getCourseManagementMapListForQuery(numName,type);

        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/courseManagementDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseManagementDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        CourseManagement s= null;
        Optional<CourseManagement> op;
        if(id != null) {
            op= courseManagementRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            courseManagementRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }



    @PostMapping("/courseManagementEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseManagementEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        CourseManagement cm = null;
        Student s;
        Course c;
        Optional<CourseManagement> op;
        if (id != null) {
            op = courseManagementRepository.findById(id);
            if (op.isPresent()) {
                cm = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for (i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("label", s.getStudentName());
            m.put("value", s.getId());
            studentIdList.add(m);
        }
        List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for (i = 0; i < cList.size(); i++) {
            c = cList.get(i);
            m = new HashMap();
            m.put("label", c.getCourseName());
            m.put("value", c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId", "");
        form.put("courseId", "");
        if (cm != null) {
            form.put("id", cm.getId());
            form.put("studentId", cm.getStudent().getId());
            form.put("courseId", cm.getCourse().getId());

        }
        form.put("studentIdList", studentIdList);
        form.put("courseIdList", courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewCourseManagementId() {
        Integer id = courseManagementRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }



    @PostMapping("/courseManagementEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseManagementEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentName = CommonMethod.getInteger(form, "studentId");
        Integer courseName = CommonMethod.getInteger(form, "courseId");
        CourseManagement cm = null;
        Student s = null;
        Course c = null;
        Optional<CourseManagement> op;
        if (id != null) {
            op = courseManagementRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                cm = op.get();
            }
        }
        if (cm == null) {
            cm = new CourseManagement();   //不存在 创建实体对象
            id = getNewCourseManagementId(); //获取鑫的主键，这个是线程同步问题;
            cm.setId(id);  //设置新的id
        }
        cm.setStudent(studentRepository.findById(studentName).get());  //设置属性
        cm.setCourse(courseRepository.findById(courseName).get());

        courseManagementRepository.save(cm);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(cm.getId());  // 将记录的id返回前端
    }



}
