package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;


    public List getCourseMapList(String numName) {
        List dataList = new ArrayList();
        List<Course> courseList = courseRepository.findAll();  //数据库查询操作
        if (courseList == null || courseList.size() == 0)
            return dataList;
        Course course;
        Map m;
        String courseNameParas;
        String attendInfParas;
        String scoreParas;
        for (int i = 0; i < courseList.size(); i++) {
            course = courseList.get(i);
            m = new HashMap();
            m.put("id", course.getId());
            m.put("courseNum", course.getCourseNum());
            courseNameParas="model=courseCenter&courseName="+course.getCourseName();
            attendInfParas="model=attendInf&courseName="+course.getCourseName();
            scoreParas="model=score&courseName="+course.getCourseName();
            m.put("scoreParas",scoreParas);
            m.put("courseName", course.getCourseName());
            m.put("credit", course.getCredit());
            m.put("teacher", course.getTeacher());
            m.put("type", course.getType());
            m.put("courseNameParas",courseNameParas);
            m.put("attendInfParas",attendInfParas);
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/courseInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInit() {
        List dataList = getCourseMapList("");
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/courseDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course h = null;
        Optional<Course> op;
        if (id != null) {
            op = courseRepository.findById(id);
            if (op.isPresent()) {
                h = op.get();
            }
        }
        if (h != null) {
            courseRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/courseEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course course = null;
        Optional<Course> op;
        if (id != null) {
            op = courseRepository.findById(id);
            if (op.isPresent()) {
                course = op.get();
            }
        }
        Map form = new HashMap();
        if (course != null) {
            form.put("id", course.getId());
            form.put("courseNum", course.getCourseNum());
            form.put("courseName", course.getCourseName());
            form.put("credit", course.getCredit());
            form.put("preCourse", course.getPreCourse());
            form.put("teacher", course.getTeacher());
            form.put("type", course.getType());
            form.put("time", course.getTime());
            form.put("hours", course.getHours());
            form.put("place", course.getPlace());
        }
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象

    }

    public synchronized Integer getNewCourseId() {
        Integer id = courseRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/courseEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer credit = CommonMethod.getInteger(form, "credit");
        String courseName = CommonMethod.getString(form, "courseName");
        String courseNum = CommonMethod.getString(form, "courseNum");
        String preCourse = CommonMethod.getString(form, "preCourse");
        String teacher = CommonMethod.getString(form, "teacher");
        String type = CommonMethod.getString(form, "type");
        String time = CommonMethod.getString(form, "time");
        String hours = CommonMethod.getString(form, "hours");
        String place = CommonMethod.getString(form, "place");
        Course course = null;
        Optional<Course> op;
        if (id != null) {
            op = courseRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                course = op.get();
            }
        }
        if (course == null) {
            course = new Course();   //不存在 创建实体对象
            id = getNewCourseId(); //获取鑫的主键，这个是线程同步问题;
            course.setId(id);  //设置新的id
        }
        course.setCourseNum(courseNum);  //设置属性
        course.setCourseName(courseName);
        course.setPreCourse(preCourse);
        course.setTeacher(teacher);
        course.setCredit(credit);
        course.setType(type);
        course.setTime(time);
        course.setHours(hours);
        course.setPlace(place);
        courseRepository.save(course);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(course.getId());  // 将记录的id返回前端
    }


    public List getCourseMapListForQuery(String numName, String type,String creditOrder) {
        List dataList = new ArrayList();
        //数据库查询操作
        List<Course> courseList = courseRepository.findByNumNameType(numName, type);
        if (creditOrder != null&&creditOrder!="") {
            if (creditOrder.equals("学分降序")) {
                courseList = courseRepository.findByNumNameTypeCreditDescend(numName, type);
            } else if (creditOrder.equals("学分升序")) {
                courseList = courseRepository.findByNumNameTypeCreditAscend(numName, type);
            }
        }
        if (courseList == null || courseList.size() == 0)
            return dataList;
        Student s;
        Course course;
        Map m;
//        String courseParas, studentNameParas;
        for (int i = 0; i < courseList.size(); i++) {
            course = courseList.get(i);
            m = new HashMap();
            m.put("id", course.getId());
            m.put("courseNum", course.getCourseNum());
            m.put("courseName", course.getCourseName());
            m.put("credit", course.getCredit());
            m.put("teacher", course.getTeacher());
            m.put("type", course.getType());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/courseQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        String type = dataRequest.getString("type");
        String creditOrder = dataRequest.getString("creditOrder");

        if (numName == null) numName = "";
        if (type == null) type = "";
        if (creditOrder == null) creditOrder = "";
        List dataList = getCourseMapListForQuery(numName, type,creditOrder);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

}
