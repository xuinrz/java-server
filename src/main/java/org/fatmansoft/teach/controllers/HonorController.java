package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
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
public class HonorController {
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    public List getHonorMapList(String numName) {
        List dataList = new ArrayList();
        List<Honor> honorList = honorRepository.findAll();  //数据库查询操作
        if (honorList == null || honorList.size() == 0)
            return dataList;
        Student s;
        Honor h;
        Map m;
//        String courseParas, studentNameParas;
        for (int i = 0; i < honorList.size(); i++) {
            h = honorList.get(i);
            s = h.getStudent();
            m = new HashMap();
            m.put("id", h.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            if (h.getHonorLevel().equals("国家级")) {
                m.put("honorLevel", "国家级");
            } else if (h.getHonorLevel().equals("省级")) {
                m.put("honorLevel", "省级");
            } else if (h.getHonorLevel().equals("校级")){
                m.put("honorLevel", "校级");
            }
            m.put("honorName", h.getHonorName());
            m.put("honorDate", DateTimeTool.parseDateTime(h.getHonorDate(), "yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/honorInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorInit() {
        List dataList = getHonorMapList("");
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/honorDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse honorDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Honor h= null;
        Optional<Honor> op;
        if(id != null) {
            op= honorRepository.findById(id);
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            honorRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/honorEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Honor honor= null;
        Student s;

        Optional<Honor> op;
        if(id != null) {
            op= honorRepository.findById(id);
            if(op.isPresent()) {
                honor = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName()+"("+s.getStudentNum()+")");
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        if(honor != null) {
            form.put("id",honor.getId());
            form.put("studentId",honor.getStudent().getId());
            if (honor.getHonorLevel().equals("国家级")) {
                form.put("honorLevel", "国家级");
            } else if (honor.getHonorLevel().equals("省级")) {
                form.put("honorLevel", "省级");
            } else if (honor.getHonorLevel().equals("校级")){
                form.put("honorLevel", "校级");
            }
            form.put("honorName", honor.getHonorName());
            form.put("honorDate", DateTimeTool.parseDateTime(honor.getHonorDate(), "yyyy-MM-dd"));
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form);
    }
    public synchronized Integer getNewHonorId(){
        Integer  id = honorRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/honorEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse honorEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        String honorLevel = CommonMethod.getString(form,"honorLevel");
        String honorName = CommonMethod.getString(form,"honorName");
        Date honorDate = CommonMethod.getDate(form,"honorDate");
        Honor honor= null;
        Student s= null;
        Optional<Honor> op;
        if(id != null) {
            op= honorRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                honor = op.get();
            }
        }
        if(honor == null) {
            honor = new Honor();   //不存在 创建实体对象
            id = getNewHonorId(); //获取鑫的主键，这个是线程同步问题;
            honor.setId(id);  //设置新的id
        }
        honor.setStudent(studentRepository.findById(studentId).get());  //设置属性
        honor.setHonorLevel(honorLevel);
        honor.setHonorName(honorName);
        honor.setHonorDate(honorDate);
        honorRepository.save(honor);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(honor.getId());  // 将记录的id返回前端
    }






    public List getHonorMapListForQuery(String numName,String level) {
        List dataList = new ArrayList();
        List<Honor> honorList = honorRepository.findByNumNameLevel(numName,level);  //数据库查询操作
        if (honorList == null || honorList.size() == 0)
            return dataList;
        Student s;
        Honor h;
        Map m;
//        String courseParas, studentNameParas;
        for (int i = 0; i < honorList.size(); i++) {
            h = honorList.get(i);
            s = h.getStudent();
            m = new HashMap();
            m.put("id", h.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            if (h.getHonorLevel().equals("国家级")) {
                m.put("honorLevel", "国家级");
            } else if (h.getHonorLevel().equals("省级")) {
                m.put("honorLevel", "省级");
            } else if (h.getHonorLevel().equals("校级")){
                m.put("honorLevel", "校级");
            }
            m.put("honorName", h.getHonorName());
            m.put("honorDate", DateTimeTool.parseDateTime(h.getHonorDate(), "yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/honorQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String level = dataRequest.getString("level");
        if(numName==null)numName="";
        if(level ==null)level = "级";
        List dataList = getHonorMapListForQuery(numName,level);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

}
