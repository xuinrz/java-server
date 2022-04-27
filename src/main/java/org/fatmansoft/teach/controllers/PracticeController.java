package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Practice;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.PracticeRepository;
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
public class PracticeController {
    @Autowired
    private PracticeRepository practiceRepository;
    @Autowired
    private StudentRepository studentRepository;

    public List getPracticeMapList(String numName) {
        List dataList = new ArrayList();
        List<Practice> practiceList = practiceRepository.findAll();  //数据库查询操作
        if (practiceList == null || practiceList.size() == 0)
            return dataList;
        Student s;
        Practice practice;
        Map m;
//        String courseParas, studentNameParas;
        for (int i = 0; i < practiceList.size(); i++) {
            practice = practiceList.get(i);
            s = practice.getStudent();
            m = new HashMap();
            m.put("id", practice.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());

            if (practice.getpType().equals("社会实践")) {
                m.put("pType", "社会实践");
            } else if (practice.getpType().equals("学科竞赛")) {
                m.put("pType", "学科竞赛");
            } else if (practice.getpType().equals("科技成果")) {
                m.put("pType", "科技成果");
            } else if (practice.getpType().equals("培训讲座")) {
                m.put("pType", "培训讲座");
            } else if (practice.getpType().equals("创新项目")) {
                m.put("pType", "创新项目");
            } else if (practice.getpType().equals("校外实习")) {
                m.put("pType", "校外实习");
            }
            m.put("pName", practice.getpName());
            m.put("pTeacher", practice.getpTeacher());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/practiceInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceInit() {
        List dataList = getPracticeMapList("");
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/practiceDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse practiceDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Practice h = null;
        Optional<Practice> op;
        if (id != null) {
            op = practiceRepository.findById(id);
            if (op.isPresent()) {
                h = op.get();
            }
        }
        if (h != null) {
            practiceRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/practiceEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Practice practice = null;
        Student s;

        Optional<Practice> op;
        if (id != null) {
            op = practiceRepository.findById(id);
            if (op.isPresent()) {
                practice = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for (i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("label", s.getStudentName() + "(" + s.getStudentNum() + ")");
            m.put("value", s.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId", "");
        if (practice != null) {
            form.put("id", practice.getId());
            form.put("studentId", practice.getStudent().getId());
            if (practice.getpType().equals("社会实践")) {
                form.put("pType", "社会实践");
            } else if (practice.getpType().equals("学科竞赛")) {
                form.put("pType", "学科竞赛");
            } else if (practice.getpType().equals("科技成果")) {
                form.put("pType", "科技成果");
            } else if (practice.getpType().equals("培训讲座")) {
                form.put("pType", "培训讲座");
            } else if (practice.getpType().equals("创新项目")) {
                form.put("pType", "创新项目");
            } else if (practice.getpType().equals("校外实习")) {
                form.put("pType", "校外实习");
            }
            form.put("pName", practice.getpName());
            form.put("pTeacher", practice.getpTeacher());
        }
        form.put("studentIdList", studentIdList);
        return CommonMethod.getReturnData(form);
    }

    public synchronized Integer getNewPracticeId() {
        Integer id = practiceRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }


    @PostMapping("/practiceEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse practiceEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        String pType = CommonMethod.getString(form, "pType");
        String pName = CommonMethod.getString(form, "pName");
        String pTeacher = CommonMethod.getString(form, "pTeacher");
        Practice practice = null;
        Student s = null;
        Optional<Practice> op;
        if (id != null) {
            op = practiceRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                practice = op.get();
            }
        }
        if (practice == null) {
            practice = new Practice();   //不存在 创建实体对象
            id = getNewPracticeId(); //获取鑫的主键，这个是线程同步问题;
            practice.setId(id);  //设置新的id
        }
        practice.setStudent(studentRepository.findById(studentId).get());  //设置属性
        practice.setpType(pType);
        practice.setpName(pName);
        practice.setpTeacher(pTeacher);
        practiceRepository.save(practice);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(practice.getId());  // 将记录的id返回前端
    }


    public List getPracticeMapListForQuery(String numName, String level) {
        List dataList = new ArrayList();
        List<Practice> practiceList = practiceRepository.findByNumNameType(numName, level);  //数据库查询操作
        if (practiceList == null || practiceList.size() == 0)
            return dataList;
        Student s;
        Practice practice;
        Map m;
//        String courseParas, studentNameParas;
        for (int i = 0; i < practiceList.size(); i++) {
            practice = practiceList.get(i);
            s = practice.getStudent();
            m = new HashMap();
            m.put("id", practice.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            if (practice.getpType().equals("社会实践")) {
                m.put("pType", "社会实践");
            } else if (practice.getpType().equals("学科竞赛")) {
                m.put("pType", "学科竞赛");
            } else if (practice.getpType().equals("科技成果")) {
                m.put("pType", "科技成果");
            } else if (practice.getpType().equals("培训讲座")) {
                m.put("pType", "培训讲座");
            } else if (practice.getpType().equals("创新项目")) {
                m.put("pType", "创新项目");
            } else if (practice.getpType().equals("校外实习")) {
                m.put("pType", "校外实习");
            }
            m.put("pName", practice.getpName());
            m.put("pTeacher", practice.getpTeacher());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/practiceQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        String type = dataRequest.getString("type");
        if (numName == null) numName = "";
        if (type == null) type = "";
        List dataList = getPracticeMapListForQuery(numName, type);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
}
