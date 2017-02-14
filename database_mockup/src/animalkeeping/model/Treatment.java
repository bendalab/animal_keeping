package animalkeeping.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 28.12.16.
 */
public class Treatment {
    private Long id;
    private Date start;
    private Date end;
    private Subject subject;
    private Person person;
    private TreatmentType type;
    private Set<TreatmentNote> notes = new HashSet<>(0);
    public Treatment() {}

    public Treatment(Date start, Subject subject, Person person, TreatmentType type) {
        this.start = start;
        this.subject = subject;
        this.person = person;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public TreatmentType getType() {
        return type;
    }

    public void setType(TreatmentType type) {
        this.type = type;
    }

    public Set<TreatmentNote> getNotes() {
        return notes;
    }

    public void setNotes(Set<TreatmentNote> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "id=" + id +
                ", type=" + type +
                ", start=" + start +
                ", end=" + ((end != null) ? end : "") +
                ", subject=" + subject +
                ", person=" + person +
                '}';
    }
}
