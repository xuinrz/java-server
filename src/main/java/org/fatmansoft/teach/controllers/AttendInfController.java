package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.models.AttendInf;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.AttendInfRepository;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class AttendInfController {
    @Autowired
    private AttendInfRepository attendInfRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    public List getAttendInfMapList(String name) {
        List dataList = new ArrayList();
        List<AttendInf> sList = attendInfRepository.findByCourseCourseName(name);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        AttendInf sc;
        Student s;
        Course c;
        Map m;
        String courseParas, studentNameParas;
        for (int i = 0; i < sList.size(); i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            m.put("courseNum", c.getCourseNum());
            m.put("courseName", c.getCourseName());
            m.put("reason", sc.getReason());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/attendInfInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendInfInit(@Valid @RequestBody DataRequest dataRequest) {

        String courseName=dataRequest.getString("courseName");
        if(courseName==null){
            List dataList = getAttendInfMapList("");
            return CommonMethod.getReturnData(dataList);
        }
        List dataList = getAttendInfMapList(courseName);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/attendInfQuery")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendInfQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName==null)numName="";
        List dataList = getAttendInfMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/attendInfEditInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendInfEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        AttendInf sc = null;
        Student s;
        Course c;
        Optional<AttendInf> op;
        if (id != null) {
            op = attendInfRepository.findById(id);
            if (op.isPresent()) {
                sc = op.get();
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
        if (sc != null) {
            form.put("id", sc.getId());
            form.put("studentId", sc.getStudent().getId());
            form.put("courseId", sc.getCourse().getId());
            form.put("reason", sc.getReason());
        }
        form.put("studentIdList", studentIdList);
        form.put("courseIdList", courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewAttendInfId() {
        Integer id = attendInfRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }


    @PostMapping("/attendInfEditSubmit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendInfEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        Integer courseId = CommonMethod.getInteger(form, "courseId");
        String reason = CommonMethod.getString(form, "reason");
        AttendInf sc = null;
        Student s = null;
        Course c = null;
        Optional<AttendInf> op;
        if (id != null) {
            op = attendInfRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                sc = op.get();
            }
        }
        if (sc == null) {
            Boolean isChosen=(scoreRepository.isCourseManagementExist(studentId,courseId).size()!=0);
            if(!isChosen) return CommonMethod.getReturnMessageError("该学生未选择该课程");
            sc = new AttendInf();   //不存在 创建实体对象
            id = getNewAttendInfId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        Boolean isChosen=(scoreRepository.isCourseManagementExist(studentId,courseId).size()!=0);
        if(!isChosen) return CommonMethod.getReturnMessageError("该学生未选择该课程");
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setReason(reason);
        attendInfRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }


    @PostMapping("/attendInfDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendInfDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        AttendInf s = null;
        Optional<AttendInf> op;
        if (id != null) {
            op = attendInfRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            attendInfRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK("333");
    }
}


