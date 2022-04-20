package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class HonorController {
    @Autowired
    private HonorRepository honorRepository;

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
            m.put("studentName", s);
            if (h.getHonorLevel().equals("1")) {
                m.put("honorLevel", "国家级");
            } else if (h.getHonorLevel().equals("2")) {
                m.put("honorLevel", "省级");
            } else {
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


}
