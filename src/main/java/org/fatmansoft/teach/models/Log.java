package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "log",
        uniqueConstraints = {
        })
public class Log
{
    @Id
    private Integer id;
    private Date datetime;
    @NotBlank
    @Size(max = 50)
    private String matters;
    private String category;

   @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "StudentId")
    private Student student;

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

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getMatters() {
        return matters;
    }

    public void setMatters(String matters) {
        this.matters = matters;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

