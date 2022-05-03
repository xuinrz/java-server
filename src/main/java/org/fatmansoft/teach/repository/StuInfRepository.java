package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.AttendInf;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface StuInfRepository extends JpaRepository<Score,Integer> {
    @Query(value="from Score s where s.student.studentName=?1 ")
    List<Score> findScoreByStudentName(String name);
    @Query(value="select count(s) from AttendInf s where s.student.studentName=?1 ")
    Integer findAbsenceCountByStudentName(String name);
    @Query(value="select count(s) from Honor s where s.student.studentName=?1")
    Integer findAwardCountByStudentName(String name);
}


