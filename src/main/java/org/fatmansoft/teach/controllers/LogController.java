package org.fatmansoft.teach.controllers;
import org.fatmansoft.teach.models.Log;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.LogRepository;
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

public class LogController
{
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LogRepository logRepository;


    public List getLogMapList(Integer numName) {
        List dataList = new ArrayList();
        List<Log> sList = logRepository.findLogListByStudentId(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Log sc;
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
    @PostMapping("/logInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse logInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId==null)
        {
            studentId=0;
        }
        List dataList = getLogMapList(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getLogMapListForQuery(String numName,String type) {
        List dataList = new ArrayList();
        List<Log> logList = logRepository.findByNumNameType(numName,type);  //数据库查询操作
        System.out.println(logList);
        if (logList == null || logList.size() == 0)
            return dataList;
        Student s;
        Log sc;
        Map m;
        for(int i = 0; i < logList.size();i++) {
            sc = logList.get(i);
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

    @PostMapping("/logQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse logQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String type = dataRequest.getString("category");
        if(numName==null)
            numName="";
        if(type ==null)
            type= "";
        List dataList = getLogMapListForQuery(numName,type);
        System.out.println(numName+type);

        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/logDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse logDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Log s= null;
        Optional<Log> op;
        if(id != null) {
            op= logRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            logRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/logEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse logEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");//id
        Log sc= null;//Score 变量
        Student s;//Student变量
        //Course c;//Course变量
        Optional<Log> op;//一个容器op
        if(id != null) { //如果找到了id
            op= logRepository.findById(id);//op指向了这些id
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
    public synchronized Integer getNewLogId(){
        Integer  id = logRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }
    @PostMapping("/logEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse logEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
     //   Integer courseId = CommonMethod.getInteger(form,"courseId");
        //Integer mark = CommonMethod.getInteger(form,"mark");
        String category=CommonMethod.getString(form,"category");
        Date datetime = CommonMethod.getDate(form,"dateTime");
        String  matters= CommonMethod.getString(form,"matters");

        Log sc= null;
        Student s= null;
        //Course c = null;
        Optional<Log> op;
        if(id != null) {
            op= logRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Log();   //不存在 创建实体对象
            id = getNewLogId(); //获取新的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        //sc.setCourse(courseRepository.findById(courseId).get());
        sc.setMatters(matters);
        sc.setDatetime(datetime);
        sc.setCategory(category);
        logRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }
}
