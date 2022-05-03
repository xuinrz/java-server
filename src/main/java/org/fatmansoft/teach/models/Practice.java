package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "practice",
        uniqueConstraints = {
        })
public class Practice {
    @Id
    private Integer id;

   @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "StudentId")
    private Student student;


    @Size(max = 5)
    private String pType;
    private String pName;
    private String pTeacher;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpTeacher() {
        return pTeacher;
    }

    public void setpTeacher(String pTeacher) {
        this.pTeacher = pTeacher;
    }
}