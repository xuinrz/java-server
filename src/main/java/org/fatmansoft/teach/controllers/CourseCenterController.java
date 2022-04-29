package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseCenter;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseCenterRepository;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class CourseCenterController {
    @Autowired
   private CourseCenterRepository courseCenterRepository;
    @Autowired
    private CourseRepository courseRepository;
    public List getCourseCenterMapList(String numName){
        List dataList =new ArrayList();
        List<CourseCenter> courseCenterList=courseCenterRepository.findCourseCenterListByNumName(numName);
        if(courseCenterList==null||courseCenterList.size()==0){
            return dataList;
        }
        CourseCenter courseCenter;
        Map m;
        Course c;
        for(int i=0;i<courseCenterList.size();i++){
            courseCenter=courseCenterList.get(i);
            c=courseCenter.getCourse();
            m=new HashMap();

            m.put("id",courseCenter.getId());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("textbook",courseCenter.getTextbook());
            m.put("courseWare",courseCenter.getCourseWare());
            m.put("reference",courseCenter.getReference());
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/courseCenterInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseCenterInit(@Valid @RequestBody DataRequest dataRequest){
        String courseName=dataRequest.getString("courseName");
        if(courseName==null){
            List dataList = getCourseCenterMapList("");
            return CommonMethod.getReturnData(dataList);
        }
        List dataList = getCourseCenterMapList(courseName);
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/courseCenterQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseCenterQuery(@Valid @RequestBody DataRequest dataRequest){
        String numName=dataRequest.getString("numName");
        if (numName==null)numName="";
        List dataList=getCourseCenterMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/courseCenterEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseCenterEditInit(@Valid @RequestBody DataRequest dataRequest){
        Integer id=dataRequest.getInteger("id");
        CourseCenter courseCenter=null;
        Optional<CourseCenter> op;
        Course c;
        if(id != null) {
            op= courseCenterRepository.findById(id);
            if(op.isPresent()) {
                courseCenter = op.get();
            }
        }
        int i;
        Map m;
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
        //form.put("studentId","");
        form.put("courseId","");
        if(courseCenter != null) {
            form.put("id",courseCenter.getId());
            form.put("courseId",courseCenter.getCourse().getId());
            form.put("courseware",courseCenter.getCourseWare());
            form.put("reference",courseCenter.getReference());
        }
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/courseCenterDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseCenterDelete(@Valid @RequestBody DataRequest dataRequest){
        Integer id=dataRequest.getInteger("id");
        CourseCenter courseCenter=null;
        Optional<CourseCenter> op;
        if(id!=null){
            op=courseCenterRepository.findById(id);
            if(op.isPresent()){
                courseCenter=op.get();
            }

        }
        if(courseCenter!=null){
            courseCenterRepository.delete(courseCenter);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/courseCenterEditSubmit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseCenterEditSubmit(@Valid @RequestBody DataRequest dataRequest){
        Map form=dataRequest.getMap("form");
        Integer id = CommonMethod.getInteger(form, "id");
        Integer courseId=CommonMethod.getInteger(form,"courseId");
        String textbook=CommonMethod.getString(form,"textbook");
        String courseWare=CommonMethod.getString(form,"courseWare");
        String reference=CommonMethod.getString(form,"reference");
        CourseCenter courseCenter=null;
        Optional<CourseCenter> op;
        Course c=null;
        if(id!=null){
            op=courseCenterRepository.findById(id);
            if(op.isPresent()){
                courseCenter=op.get();
            }
        }
        if(courseCenter==null){
            courseCenter=new CourseCenter();
            id=courseCenterRepository.getMaxId();
            if(id==null){
                id=1;
            }
            else{id++;}
            courseCenter.setId(id);

        }
        courseCenter.setCourse(courseRepository.findById(courseId).get());
        courseCenter.setTextbook(textbook);
        courseCenter.setCourseWare(courseWare);
        courseCenter.setReference(reference);
        courseCenterRepository.save(courseCenter);
        return CommonMethod.getReturnData(courseCenter.getId());


    }





}
