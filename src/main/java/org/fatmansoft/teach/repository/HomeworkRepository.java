package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Integer> {
    @Query(value = "select max(id) from Homework  ")
    Integer getMaxId();
    @Query("from Homework h where h.course.id=?1 or ?1=0 order by h.submission ")
    List<Homework> findByCourseId(Integer courseId);
    List<Homework> findByCourseCourseNum(String courseNum);
    @Query(value="select s.course from Homework s where s.student.id=?1")
    List<Course> findCourseList(Integer studentId);

}
