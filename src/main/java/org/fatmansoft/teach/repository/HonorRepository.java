package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {
    @Query("select max(id) from Honor")
    Integer getMaxId();

    @Query(value = " from Honor h where (?1='' or (h.student.studentNum like %?1%) or (h.student.studentName like %?1%)) and h.honorLevel like %?2%")
    List<Honor> findByNumNameLevel(String numName,String level);





}
