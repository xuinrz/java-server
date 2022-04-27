package org.fatmansoft.teach.controllers;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Daily;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.DailyRepository;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class DailyController
{
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DailyRepository dailyRepository;


    public List getDailyMapList(String numName) {
        List dataList = new ArrayList();
        List<Daily> sList = dailyRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Daily sc;
        Student s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());

            m.put("matters",sc.getMatters());
            m.put("dateTime",DateTimeTool.parseDateTime(sc.getDatetime(),"yyyy-MM-dd"));
            if("日常消费".equals(sc.getCategory())) {    //数据库存的是编码，显示是名称
                m.put("category","日常消费");
            }else if("外出申请".equals(sc.getCategory())) {
                m.put("category","外出申请");
            }
            else m.put("category","请假审批");
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/dailyInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getDailyMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getDailyMapListForQuery(String numName,String type) {
        List dataList = new ArrayList();
        List<Daily> dailyList = dailyRepository.findByNumNameType(numName,type);  //数据库查询操作
        System.out.println(dailyList);
        if (dailyList == null || dailyList.size() == 0)
            return dataList;
        Student s;
        Daily sc;
        Map m;
        for(int i = 0; i < dailyList.size();i++) {
            sc = dailyList.get(i);
            s = sc.getStudent();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());

            m.put("matters",sc.getMatters());
            m.put("dateTime",DateTimeTool.parseDateTime(sc.getDatetime(),"yyyy-MM-dd"));
            if("日常消费".equals(sc.getCategory())) {    //数据库存的是编码，显示是名称
                m.put("category","日常消费");
            }else if("外出申请".equals(sc.getCategory())) {
                m.put("category","外出申请");
            }
            else m.put("category","请假审批");
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/dailyQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String type = dataRequest.getString("category");
        if(numName==null)
            numName="";
        if(type ==null)
            type= "";
        List dataList = getDailyMapListForQuery(numName,type);
        System.out.println(numName+type);

        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/dailyDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse dailyDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Daily s= null;
        Optional<Daily> op;
        if(id != null) {
            op= dailyRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            dailyRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/dailyEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");//id
        Daily sc= null;//Score 变量
        Student s;//Student变量
        //Course c;//Course变量
        Optional<Daily> op;//一个容器op
        if(id != null) { //如果找到了id
            op= dailyRepository.findById(id);//op指向了这些id
            if(op.isPresent()) {
                sc = op.get();    //通过op的指向来让Score变量 sc获得id数据集
            }
        }
        Map m;  //一个Map型变量
        int i;
        List studentIdList = new ArrayList();  //新建一个储存StudentId的集合
        List<Student> sList = studentRepository.findAll();   // 一个指向了所有学生数据的容器
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);    //
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
       /* List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("label",c.getCourseName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }*/
        Map form = new HashMap();
        form.put("studentId","");
       // form.put("courseId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            //form.put("courseId",sc.getCourse().getId());
            if (sc.getCategory().equals("日常消费")) {
                form.put("category", "日常消费");
            } else if (sc.getCategory().equals("外出申请")) {
                form.put("category", "外出申请");
            } else if(sc.getCategory().equals("请假审批")){
                form.put("category", "请假审批");
            }
            form.put("matters",sc.getMatters());
            form.put("dateTime",DateTimeTool.parseDateTime(sc.getDatetime(),"yyyy-MM-dd"));
        }
        form.put("studentIdList",studentIdList);
     //   form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewDailyId(){
        Integer  id = dailyRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }
    @PostMapping("/dailyEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse dailyEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
     //   Integer courseId = CommonMethod.getInteger(form,"courseId");
        //Integer mark = CommonMethod.getInteger(form,"mark");
        String category=CommonMethod.getString(form,"category");
        Date datetime = CommonMethod.getDate(form,"dateTime");
        String  matters= CommonMethod.getString(form,"matters");

        Daily sc= null;
        Student s= null;
        //Course c = null;
        Optional<Daily> op;
        if(id != null) {
            op= dailyRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Daily();   //不存在 创建实体对象
            id = getNewDailyId(); //获取新的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        //sc.setCourse(courseRepository.findById(courseId).get());
        sc.setMatters(matters);
        sc.setDatetime(datetime);
        sc.setCategory(category);
        dailyRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }
}
