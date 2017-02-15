package animalkeeping.model;

import java.util.Date;

/**
 * Created by jan on 28.12.16.
 */
public class SubjectNote extends Note {
    private Subject subject;

    public SubjectNote() {
        super();
    }

    public SubjectNote(String name, String content, Date date, Subject subject) {
        super(name, content, date);
        this.subject = subject;
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
                "id=" + getId() +
                ", name='" + getName()+ '\'' +
                ", comment='" + getComment() + '\'' +
                ", date=" + getDate() +
                ", person=" + ((getPerson()!= null) ? getPerson().getLastName() : "") +
                ", subject=" + subject +
                '}';
    }
}


