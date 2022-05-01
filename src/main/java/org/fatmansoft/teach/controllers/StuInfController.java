package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StuInfRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class StuInfController {
    @Autowired
    private StuInfRepository stuInfRepository;
    @PostMapping("/stuInfInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse stuInfInit(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("stuName");
        List<Score> scoreList=stuInfRepository.findScoreByStudentName(name);
        Score score;
        double sum=0,totleCredit=0;
        int absenceCount,awardCount;
        for(int i=0;i<scoreList.size();i++){

            score=scoreList.get(i);

            int credit=score.getCourse().getCredit();
            totleCredit+=credit;
            sum+=credit*score.getGradePoint();
        }
        double x=sum/totleCredit;
        absenceCount= stuInfRepository.findAbsenceCountByStudentName(name);
        awardCount=stuInfRepository.findAwardCountByStudentName(name);
        Map m =new HashMap();
        m.put("gradePoint",x);
        m.put("stuName",name);
        m.put("absenceCount",absenceCount);
        m.put("awardCount",awardCount);
        return CommonMethod.getReturnData(m);
    }

}
