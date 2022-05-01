package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
    @Query(value = "select max(id) from Score  ")
    Integer getMaxId();

    List<Score> findByStudentId(Integer studentId);

    List<Score> findByCourseCourseNum(String courseNum);

    @Query(value = "select s.course from Score s where s.student.id=?1")
    List<Course> findCourseList(Integer studentId);

    @Query(value = " from Score s where (?1='' or (s.student.studentNum like %?1%) or (s.student.studentName like %?1%)) and (s.course.courseName like concat('%',?2,'%') or ?2='') ")
    List<Score> findByNumNameCourseName(String numName, String courseName);

    @Query(value = " from Score s where (?1='' or (s.student.studentNum like %?1%) or (s.student.studentName like %?1%)) and (s.course.courseName like concat('%',?2,'%') or ?2='') order by s.mark")
    List<Score> findByNumNameCourseNameScoreAscend(String numName, String courseName);

    @Query(value = " from Score s where (?1='' or (s.student.studentNum like %?1%) or (s.student.studentName like %?1%)) and (s.course.courseName like concat('%',?2,'%') or ?2='') order by s.mark desc ")
    List<Score> findByNumNameCourseNameScoreDescend(String numName, String courseName);

    @Query(value = " from Score s where (?1='' or (s.student.studentNum like %?1%) or (s.student.studentName like %?1%)) and (s.course.courseName like concat('%',?2,'%') or ?2='') and s.mark<60 order by s.mark desc ")
    List<Score> findByNumNameCourseNameScorefail(String numName, String courseName);

    @Query(value = "select count(id) from Score where course.id=?1")
    Integer countByCourseId(Integer courseId);

    @Query(value = " from Score s where s.student.id=?1 and s.course.id=?2 ")
    List<Score> isCourseManagementExist(Integer studentId, Integer courseId);

    @Query(value= "select cm.course from Score cm where cm.student.id=?1 or ?1=0")
    List<Course> findCourseByStudentIdOrAll(Integer studentId);

    @Query(value= "from Score cm where (?1= cm.student.id)")
    List<Score> findByStudentIdInInf(Integer studentId);

}
