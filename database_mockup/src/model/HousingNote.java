package model;

import java.util.Date;

/**
 * Created by jan on 28.12.16.
 */
public class HousingNote {
    private Long id;
    private String name;
    private String comment;
    private Date date;
    private Person person;
    private Housing housing;

    public HousingNote() {}

    public HousingNote(String name, String content, Date date, Housing housing) {
        this.name = name;
        this.comment = content;
        this.date = date;
        this.housing = housing;
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

    public Housing getHousing() {
        return housing;
    }

    public void setHousing(Housing housing) {
        this.housing = housing;
    }

    @Override
    public String toString() {
        return "HousingNote{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", person=" + ((person != null) ? person.getLastName() : "") +
                ", housing=" + housing +
                '}';
    }
}
