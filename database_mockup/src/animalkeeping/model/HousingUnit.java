package animalkeeping.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 28.12.16.
 */
public class HousingUnit {
    private Long id;
    private String name;
    private String description;
    private String dimensions;
    private HousingType housingType;
    private HousingUnit parentUnit;
    private Integer population;
    private Set<Housing> housings =
            new HashSet<Housing>(0);
    private Set<HousingUnit> childHousingUnits =
            new HashSet<HousingUnit>(0);

    public HousingUnit() {}


    public HousingUnit(String name, HousingType housingType) {
        this.name = name;
        this.housingType = housingType;
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

    public Set<HousingUnit> getChildHousingUnits() {
        return childHousingUnits;
    }

    public void setChildHousingUnits(Set<HousingUnit> childHousingUnits) {
        this.childHousingUnits = childHousingUnits;
    }

    @Override
    public String toString() {
        return "HousingUnit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", housingType=" + (housingType != null ? housingType.getName() : "") +
                ", parentUnit=" + (parentUnit != null ? parentUnit.getName() : "") +
                '}';
    }
}
