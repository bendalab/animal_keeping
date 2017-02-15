package animalkeeping.model;

import java.util.Date;

public class HousingNote extends Note {
    private Housing housing;

    public HousingNote() {
        super();
    }

    public HousingNote(String name, String content, Date date, Housing housing) {
        super(name, content, date);
        this.housing = housing;
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
                "id=" + getId() +
                ", name='" + getName()+ '\'' +
                ", comment='" + getComment() + '\'' +
                ", date=" + getDate() +
                ", person=" + ((getPerson()!= null) ? getPerson().getLastName() : "") +
                ", housing=" + housing +
                '}';
    }
}
