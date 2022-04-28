package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query("select max(id) from Course ")
    Integer getMaxId();

    @Query(value = " from Course c where (?1='' or (c.courseName like %?1%) or (c.courseNum like %?1%)) and (c.type like concat('%',?2,'%') or ?2='')")
    List<Course> findByNumNameType(String numName, String type);

    @Query(value = " from Course c where (?1='' or (c.courseName like %?1%) or (c.courseNum like %?1%)) and (c.type like concat('%',?2,'%') or ?2='') order by c.credit desc ")
    List<Course> findByNumNameTypeCreditDescend(String numName, String type);

    @Query(value = " from Course c where (?1='' or (c.courseName like %?1%) or (c.courseNum like %?1%)) and (c.type like concat('%',?2,'%') or ?2='') order by c.credit")
    List<Course> findByNumNameTypeCreditAscend(String numName, String type);
}
