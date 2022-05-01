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
    @Query(value = " from CourseManagement s where s.student.id=?1 and s.course.id=?2 ")
    List<CourseManagement> isCourseManagementExist(Integer studentId,Integer courseId);

    @Query(value = " from CourseManagement s where (?1='' or (s.student.studentNum like %?1%) or (s.student.studentName like %?1%)) and (s.course.courseName like concat('%',?2,'%') or ?2='') ")
    List<CourseManagement> findByNumNameCourseName(String numName, String courseName);

    @Query(value= "select cm.course from CourseManagement cm where cm.student.id=?1 or ?1=0")
    List<Course> findCourseByStudentIdOrAll(Integer studentId);

    @Query(value= "from CourseManagement cm where (?1= cm.student.id)")
    List<CourseManagement> findByStudentIdInInf(Integer studentId);

    @Query(value = "select count(id) from CourseManagement where course.id=?1")
    Integer countByCourseId(Integer courseId);

}
