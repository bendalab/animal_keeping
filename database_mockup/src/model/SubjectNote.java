package model;

import java.util.Date;

/**
 * Created by jan on 28.12.16.
 */
public class SubjectNote {
    private Long id;
    private String name;
    private String comment;
    private Date date;
    private Person person;
    private Subject subject;

    public SubjectNote() {}

    public SubjectNote(String name, String content, Date date, Subject subject) {
        this.name = name;
        this.comment = content;
        this.date = date;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "SubjectNote{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", person=" + ((person != null) ? person.getLastName() : "") +
                ", housing=" + subject +
                '}';
    }
}


