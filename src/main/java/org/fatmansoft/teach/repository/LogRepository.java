package org.fatmansoft.teach.repository;


import org.fatmansoft.teach.models.Log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LogRepository extends JpaRepository<Log,Integer>
{
    @Query(value = "select max(id) from Log ")
    Integer getMaxId();
    List<Log> findByStudentId(Integer studentId);
    //List<Log> findByCourseCourseNum(String courseNum);
    @Query(value = " from Log d where (?1='' or (d.student.studentNum like %?1%) or (d.student.studentName like %?1%)) and (d.category like %?2% or ?2='')")
    List<Log> findByNumNameType(String numName, String type);

    @Query(value="from Log s where s.student.id=?1 or (?1=0)" )
    List<Log> findLogListByStudentId(Integer studentId);

}
