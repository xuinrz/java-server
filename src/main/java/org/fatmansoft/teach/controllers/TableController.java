package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseCenter;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class TableController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository courseManagementRepository;


    public List getCourseMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Course> courseList = courseManagementRepository.findCourseByStudentIdOrAll(studentId);  //数据库查询操作
        if (studentId==0)courseList = courseRepository.findAll();
        if (courseList == null || courseList.size() == 0)
            return dataList;
        Course course;
        Map m=null;

        String[] time={"第一节","第二节","第三节","第四节"};
        String[] period={"8:00-9:50","10:00-12:00","14:00-15:50","16:10-18:00"};
        for(int j = 0;j< time.length;j++) {
        m = new HashMap();
        m.put("period",period[j]);
            for (int i = 0; i < courseList.size(); i++) {
                course = courseList.get(i);
                if (course.getTime().equals(time[j])) {
                    put(course, m);
                }
            }
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/tableInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse tableInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studenId = dataRequest.getInteger("studentId");
        if (studenId==null)studenId=0;
        List dataList = getCourseMapList(studenId);
        return CommonMethod.getReturnData(dataList);
    }

    private void put(Course c,Map m){
        String s = c.getDay();
        switch(s){
            case "周一":m.put("monday",c.getCourseName());break;
            case "周二":m.put("tuesday",c.getCourseName());break;
            case "周三":m.put("wednesday",c.getCourseName());break;
            case "周四":m.put("thursday",c.getCourseName());break;
            case "周五":m.put("friday",c.getCourseName());break;
        }
    }
}
