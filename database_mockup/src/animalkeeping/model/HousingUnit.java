package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 28.12.16.
 */
public class HousingUnit extends Entity implements ChangeLogInterface {
    private String name;
    private String description;
    private String dimensions;
    private HousingType housingType;
    private HousingUnit parentUnit;
    private Integer population;
    private Set<Housing> housings =
            new HashSet<Housing>(0);
    private Integer childUnitCount;
    private Set<HousingUnit> childHousingUnits =
            new HashSet<HousingUnit>(0);

    public HousingUnit() {}


    public HousingUnit(String name, HousingType housingType) {
        this.name = name;
        this.housingType = housingType;
    }

    public String getType(){
        return this.getClass().toString();
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

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public HousingType getHousingType() {
        return housingType;
    }

    public void setHousingType(HousingType housingType) {
        this.housingType = housingType;
    }

    public HousingUnit getParentUnit() {
        return parentUnit;
    }

    public void setParentUnit(HousingUnit parentUnit) {
        this.parentUnit = parentUnit;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Set<Housing> getHousings() {return housings;}

    public Set<Housing> getAllHousings(Boolean current) {
        Set<Housing> allHousings = new HashSet<Housing>(0);

        for (Housing h : getHousings()){
            if (current) {
                if (h.getEnd() == null){
                    allHousings.add(h);
                }
            }
            else {
                allHousings.add(h);
            }
        }

        Set<HousingUnit> childHouseUnits = getChildHousingUnits();

        if (!(childHouseUnits.isEmpty())) {
            for (HousingUnit child : childHouseUnits){
                allHousings.addAll(child.getAllHousings(current));
            }
        }
        return allHousings;
    }

    public void setHousings(Set<Housing> housings) {
        this.housings = housings;
    }

    public Integer getChildUnitCount() {
        return childUnitCount;
    }

    public void setChildUnitCount(Integer childUnitCount) {
        this.childUnitCount = childUnitCount;
    }

    public Set<HousingUnit> getChildHousingUnits() {
        return getChildHousingUnits(false);
    }

    public Set<HousingUnit> getChildHousingUnits(boolean recursive) {
        if (!recursive) {
            return childHousingUnits;
        }
        Set<HousingUnit> children = new HashSet<>();
        children.addAll(childHousingUnits);
        for (HousingUnit hu : childHousingUnits) {
            children.addAll(hu.getChildHousingUnits(recursive));
        }
        return children;
    }

    public void setChildHousingUnits(Set<HousingUnit> childHousingUnits) {
        this.childHousingUnits = childHousingUnits;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HousingUnit))
            return false;
        if (obj == this)
            return true;

        HousingUnit rhs = (HousingUnit) obj;
        return new EqualsBuilder().
                append(getName(), rhs.getName()).
                append(getHousingType().getName(), rhs.getHousingType().getName()).
                append(getId(), rhs.getId()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getId()).
                append(getName()).
                append(getHousingType() != null ? getHousingType().getName() : "").
                toHashCode();
    }

    @Override
    public String toString() {
        return "HousingUnit{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", dimensions='" + getDimensions() + '\'' +
                ", housingType=" + (getHousingType() != null ? getHousingType().getName() : "") +
                ", parentUnit=" + (getParentUnit() != null ? getParentUnit().getName() : "") +
                '}';
    }
}
