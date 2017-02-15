package animalkeeping.model;

import java.util.Date;

/**
 * Created by jan on 28.12.16.
 */
public class Housing extends Entity {
    private String comment;
    private java.util.Date start;
    private java.util.Date end;
    private HousingUnit housing;
    private Subject subject;

    public Housing() {}

    public Housing(Subject subject, HousingUnit housing, Date start) {
        this.start = start;
        this.housing = housing;
        this.subject = subject;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public HousingUnit getHousing() {
        return housing;
    }

    public void setHousing(HousingUnit housing) {
        this.housing = housing;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Housing{" +
                "id=" + getId() +
                ", subject=" + subject.getName() +
                "in housing=" + housing.getName() +
                "from start=" + getStart() +
                "until end=" + ((getEnd() != null) ? getEnd(): "") +
                '}';
    }
}
