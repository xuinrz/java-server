package org.fatmansoft.teach.repository;


import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Daily;

import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DailyRepository extends JpaRepository<Daily,Integer>
{
    @Query(value = "select max(id) from Daily ")
    Integer getMaxId();
    List<Daily> findByStudentId(Integer studentId);
    //List<Daily> findByCourseCourseNum(String courseNum);
    @Query(value = " from Daily d where (?1='' or (d.student.studentNum like %?1%) or (d.student.studentName like %?1%)) and (d.category like %?2% or ?2='')")
    List<Daily> findByNumNameType(String numName,String type);

    @Query(value="from Daily s where s.student.id=?1 or (?1=0)" )
    List<Daily> findDailyListByStudentId(Integer studentId);

}
