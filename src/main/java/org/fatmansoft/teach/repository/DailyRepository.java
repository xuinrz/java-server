package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Daily;


import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DailyRepository extends JpaRepository<Daily,Integer> {
//    Optional<Student> findByStudentNum(String studentNum);
//    List<Student> findByStudentName(String studentName);

    @Query(value = "select max(id) from Daily  ")
    Integer getMaxId();

    @Query(value = "from Daily d where (d.student.id =?1) ")
    List<Daily> findDailyListByStudentId(Integer studentId);


}
