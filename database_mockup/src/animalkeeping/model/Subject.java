package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import animalkeeping.logging.ChangeLogUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 27.12.16.
 */
public class Subject extends Entity implements ChangeLogInterface {
    private String name;
    private String alias;
    private Date birthday;
    private Gender gender;
    private SpeciesType speciesType;
    private SubjectType subjectType;
    private SupplierType supplier;
    private Set<Treatment> treatments =
            new HashSet<Treatment>(0);
    private Set<SubjectNote> notes =
            new HashSet<SubjectNote>(0);
    private Set<Housing> housings =
            new HashSet<Housing>(0);
    private Housing currentHousing;

    public Subject() {}

    public Subject(String name, SpeciesType speciesType, SubjectType subjectType) {
        this.name = name;
        this.speciesType = speciesType;
        this.subjectType = subjectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public SpeciesType getSpeciesType() {
        return speciesType;
    }

    public void setSpeciesType(SpeciesType speciesType) {
        this.speciesType = speciesType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public SupplierType getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierType supplier) {
        this.supplier = supplier;
    }

    public Set<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<Treatment> treatments) {
        this.treatments = treatments;
    }

    public Set<Housing> getHousings() {
        return housings;
    }

    public void setHousings(Set<Housing> housings) {
        this.housings = housings;
    }

    public Housing getCurrentHousing() {
        for (Housing h : getHousings()) {
            if (h.getEnd() == null) {
                return h;
            }
        }
        return null;
    }

    public Set<SubjectNote> getNotes() {
        return notes;
    }

    public void setNotes(Set<SubjectNote> notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Subject))
            return false;
        if (obj == this)
            return true;

        Subject rhs = (Subject) obj;
        return new EqualsBuilder().
                append(getName(), rhs.getName()).
                append(getSpeciesType().getName(), rhs.getSpeciesType().getName()).
                append(getId(), rhs.getId()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getId()).
                append(getName()).
                append(getSpeciesType().getName()).
                toHashCode();
    }


    @Override
    public String toString() {
        return "Subject{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", speciesType=" + speciesType.getName() +
                ", subjectType=" + subjectType.getName() +
                ", supplier=" + supplier.getName() +
                '}';
    }

    @Override
    public String getType(){
        return this.getClass().toString();
    }
}
