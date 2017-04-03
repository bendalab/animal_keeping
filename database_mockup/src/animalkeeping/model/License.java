package animalkeeping.model;

/**
 * Created by jan on 27.12.16.
 */
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class License {
    private Long id;
    private String name;
    private String agency;
    private String number;
    private Person responsiblePerson;
    private Person deputy;
    private Date startDate;
    private Date endDate;
    private Set<Quota> quotas =
            new HashSet<>(0);
    private Set<TreatmentType> treatmentTypes =
            new HashSet<>(0);


    public License() { }

    public License(String name, String number, Date startDate) {
        this.name = name;
        this.number = number;
        this.startDate = startDate;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<Quota> getQuotas() {
        return quotas;
    }

    public void setQuotas(Set<Quota> quota) {
        this.quotas = quota;
    }

    public Set<TreatmentType> getTreatmentTypes() {
        return treatmentTypes;
    }

    public void setTreatmentTypes(Set<TreatmentType> treatmentTypes) {
        this.treatmentTypes = treatmentTypes;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public Person getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(Person responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public Person getDeputy() {
        return deputy;
    }

    public void setDeputy(Person deputy) {
        this.deputy = deputy;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof License))
            return false;
        if (obj == this)
            return true;

        License rhs = (License) obj;
        return new EqualsBuilder().
                        append(getName(), rhs.getName()).
                        append(getNumber(), rhs.getNumber()).
                        append(getId(), rhs.getId()).
                        isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getId()).
                append(getName()).
                append(getNumber()).
                toHashCode();
    }

    @Override
    public String toString() {
        return "License{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
