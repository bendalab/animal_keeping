package model;

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

    @Override
    public String toString() {
        return "HousingUnit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", housingType=" + housingType.getName() +
                ", parentUnit=" + ((parentUnit != null) ? parentUnit.getName() : "") +
                '}';
    }
}
