package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Daily;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.DailyRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.fatmansoft.teach.models.Student;
import org.springframework.web.servlet.support.JstlUtils;

import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class DailyController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private IntroduceService introduceService;


    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getDailyMapList(Integer studentId) {
        List dataList = new ArrayList();
        //数据库查询操作
        List<Daily> sList = dailyRepository.findDailyListByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        Daily s;
        Student sc;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            sc=s.getStudent();
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",sc.getStudentNum());
            m.put("studentName",sc.getStudentName());
            if("体育活动".equals(s.getActivity())) {    //数据库存的是编码，显示是名称
                m.put("activity","体育活动");
            }else  if ("外出旅游".equals(s.getActivity())){
                m.put("activity","外出旅游");
            }else  if ("文艺演出".equals(s.getActivity())){
                m.put("activity","文艺演出");
            }else  if ("聚会".equals(s.getActivity())){
                m.put("activity","聚会");
            }else{
                m.put("activity","其他");
            }
            m.put("activityName",s.getActivityName());
            m.put("activityDate",DateTimeTool.parseDateTime(s.getActivityDate(),"yyyy-MM-dd"));
            dataList.add(m);
        }
        return dataList;
    }
    //student页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/dailyInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId==null)studentId=0;
        List dataList = getDailyMapList(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
//    //student页面点击查询按钮请求
//    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
//    @PostMapping("/dailyQuery")
//    @PreAuthorize("hasRole('ADMIN')")
//    public DataResponse dailyQuery(@Valid @RequestBody DataRequest dataRequest) {
//        String numName= dataRequest.getString("numName");
//        if(numName==null)numName="";
//        List dataList = getDailyMapList(numName);
//        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
//    }


    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,在前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）
    @PostMapping("/dailyEditInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Daily s= null;
        Student sc;
        Optional<Daily> op;
        if(id != null) {
            op= dailyRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            sc =sList.get(i);
            m = new HashMap();
            m.put("label",sc.getStudentName());
            m.put("value",sc.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        if(s != null) {
            form.put("id",s.getId());
            form.put("studentId",s.getStudent().getId());
            form.put("activity",s.getActivity());  //这里不需要转换
            form.put("activityName",s.getActivityName());
            form.put("activityDate", DateTimeTool.parseDateTime(s.getActivityDate(),"yyyy-mm-dd")); //这里需要转换为字符串
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    @PostMapping("/dailyEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse dailyEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        String activity = CommonMethod.getString(form,"activity");
        String activityName = CommonMethod.getString(form,"activityName");
        Date activityDate = CommonMethod.getDate(form,"activityDate");
        Daily s= null;
        Student sc = null;
        Optional<Daily> op;
        if(id != null) {
            op= dailyRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Daily();   //不存在 创建实体对象
            id = dailyRepository.getMaxId();  // 查询最大的id
            if(id == null)
                id = 1;
            else
                id = id+1;
            s.setId(id);  //设置新的id
        }
        s.setStudent(studentRepository.findById(studentId).get());
        s.setActivity(activity);
        s.setActivityName(activityName);
        s.setActivityDate(activityDate);
        dailyRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
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
                System.out.println(2);
                s = op.get();
            }
        }
        if(s != null) {
            System.out.println(1);
            dailyRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //  学生个人简历页面
    //在系统在主界面内点击个人简历，后台准备个人简历所需要的各类数据组成的段落数据，在前端显示
//    @PostMapping("/getStudentIntroduceData")
//    @PreAuthorize(" hasRole('ADMIN')")
//    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
//        Map data = introduceService.getIntroduceDataMap();
//        return CommonMethod.getReturnData(data);  //返回前端个人简历数据
//    }

}

