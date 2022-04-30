package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Daily;
import org.fatmansoft.teach.models.Practice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PracticeRepository extends JpaRepository<Practice, Integer> {
    @Query("select max(id) from Practice")
    Integer getMaxId();

    @Query(value = " from Practice p where (?1='' or (p.student.studentNum like %?1%) or (p.student.studentName like %?1%)) and (p.pType like %?2% or ?2='')")
    List<Practice> findByNumNameType(String numName,String type);

    @Query(value="from Practice p where p.student.id=?1 or (?1=0)" )
    List<Practice> findPracticeListByStudentId(Integer studentId);

}
