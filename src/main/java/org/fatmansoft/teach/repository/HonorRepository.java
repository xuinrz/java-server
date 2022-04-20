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

    List<Honor> findByStudentId(Integer studentId);

    @Query("from Honor order by student.id")
    List<Honor> findAll1();



}
