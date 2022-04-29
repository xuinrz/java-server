package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.CourseCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CourseCenterRepository extends JpaRepository<CourseCenter,Integer> {
    @Query(value="select max(id) from CourseCenter ")
    Integer getMaxId();
    @Query(value="FROM CourseCenter s where ?1='' or s.course.courseName like %?1%")
    List<CourseCenter> findCourseCenterListByNumName(String numName);


}
