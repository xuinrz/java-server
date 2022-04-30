package org.fatmansoft.teach.repository;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseManagement;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CourseManagementRepository extends JpaRepository<CourseManagement,Integer>
{
    @Query(value = "select max(id) from CourseManagement ")
    Integer getMaxId();
    List<CourseManagement> findByStudentId(Integer studentId);
    //List<Daily> findByCourseCourseNum(String courseNum);
    @Query(value = " from Log d where (?1='' or (d.student.studentNum like %?1%) or (d.student.studentName like %?1%)) and (d.category like %?2% or ?2='')")
    List<CourseManagement> findByNumNameType(String numName,String type);

    @Query(value = " from CourseManagement s where (?1='' or (s.student.studentNum like %?1%) or (s.student.studentName like %?1%)) and (s.course.courseName like concat('%',?2,'%') or ?2='') ")
    List<CourseManagement> findByNumNameCourseName(String numName, String courseName);
}
