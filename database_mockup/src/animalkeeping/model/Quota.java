package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by jan on 28.12.16.
 */
public class Quota extends Entity implements ChangeLogInterface{
    private Long id;
    private Long number;
    private Gender gender;
    private SpeciesType speciesType;
    private License license;
    private Long used;

    public Quota() {}

    public Quota(Long number, SpeciesType speciesType, License license) {
        this.number = number;
        this.speciesType = speciesType;
        this.license = license;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public SpeciesType getSpeciesType() {
        return speciesType;
    }

    public void setSpeciesType(SpeciesType speciesType) {
        this.speciesType = speciesType;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public String getType(){
        return this.getClass().toString();
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getAvailableFraction() {
        if (getNumber() == null || getNumber() == 0 || getUsed() == null) {
            return 0.0;
        }
        return 1.0 - ((double)getUsed()/(double)getNumber());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Quota))
            return false;
        if (obj == this)
            return true;

        Quota rhs = (Quota) obj;
        return new EqualsBuilder().
                append(getSpeciesType().getName(), rhs.getSpeciesType().getName()).
                append(getLicense().getName(), rhs.getLicense().getName()).
                append(getId(), rhs.getId()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getId()).
                append(getSpeciesType().getName()).
                append(getLicense().getName()).
                toHashCode();
    }

    @Override
    public String toString() {
        return "Quota{" +
                "number=" + number +
                ", speciesType=" + (speciesType != null ? speciesType.getName() : " ") +
                ", license=" + (license != null ? license.getName() : " ") +
                '}';
    }
}
