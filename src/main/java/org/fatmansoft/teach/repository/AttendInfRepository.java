package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.AttendInf;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AttendInfRepository extends JpaRepository<AttendInf,Integer> {
    @Query(value="select max(id) from AttendInf ")
    Integer getMaxId();
    List<AttendInf> findByStudentId(Integer studentId);
    @Query(value = "from AttendInf a where a.course.courseName like concat('%',?1,'%') or ?1=''")
    List<AttendInf> findByCourseCourseName(String courseNum);

    @Query(value="select s.course from AttendInf s where s.student.id=?1")
    List<AttendInf> findAttendInf(Integer studentId);

}
