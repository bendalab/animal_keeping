package model;

import java.util.Date;


public class TreatmentNote {
    private Long id;
    private String name;
    private String comment;
    private Date date;
    private Person person;
    private Treatment treatment;

    public TreatmentNote() {}

    public TreatmentNote(String name, Date date, Treatment treatment) {
        this.name = name;
        this.date = date;
        this.treatment = treatment;
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

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    @Override
    public String toString() {
        return "TreatmentNote{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", person=" + ((person != null) ? person.getLastName() : "") +
                ", treatment=" + treatment +
                '}';
    }
}
