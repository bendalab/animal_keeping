package animalkeeping.model;

import java.util.Date;


public class TreatmentNote extends Note {
    private Treatment treatment;

    public TreatmentNote() {
        super();
    }

    public TreatmentNote(String name, String content, Date date, Treatment treatment) {
        super(name, content, date);
        this.treatment = treatment;
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
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", comment='" + getComment() + '\'' +
                ", date=" + getDate() +
                ", person=" + ((getPerson() != null) ? getPerson().getLastName() : "") +
                ", treatment=" + getTreatment() +
                '}';
    }
}
