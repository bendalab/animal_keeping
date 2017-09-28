package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 28.12.16.
 */
public class HousingType extends Entity implements ChangeLogInterface {
    private String name;
    private String description;
    private Boolean canHoldSubjects;
    private Boolean canHaveChildUnits;
    private Set<HousingUnit> housingUnits =
            new HashSet<>(0);

    public HousingType() {}

    public HousingType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCanHoldSubjects() {
        return canHoldSubjects;
    }

    public void setCanHoldSubjects(Boolean canHoldSubjects) {
        this.canHoldSubjects = canHoldSubjects;
    }

    public Boolean getCanHaveChildUnits() {
        return canHaveChildUnits;
    }

    public void setCanHaveChildUnits(Boolean canHaveChildUnits) {
        this.canHaveChildUnits = canHaveChildUnits;
    }

    public Set<HousingUnit> getHousingUnits() {
        return housingUnits;
    }

    public void setHousingUnits(Set<HousingUnit> housingUnits) {
        this.housingUnits = housingUnits;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HousingType))
            return false;
        if (obj == this)
            return true;

        HousingType rhs = (HousingType) obj;
        return new EqualsBuilder().
                append(getName(), rhs.getName()).
                append(getId(), rhs.getId()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getId()).
                append(getName()).
                toHashCode();
    }

    @Override
    public String toString() {
        return "HousingType{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public String getType() {
        return this.getClass().toString();
    }
}
