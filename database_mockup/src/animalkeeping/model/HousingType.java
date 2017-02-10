package animalkeeping.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 28.12.16.
 */
public class HousingType {
    private Long id;
    private String name;
    private String description;
    private Set<HousingUnit> housingUnits =
            new HashSet<>(0);

    public HousingType() {}

    public HousingType(String name, String description) {
        this.name = name;
        this.description = description;
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

    public Set<HousingUnit> getHousingUnits() {
        return housingUnits;
    }

    public void setHousingUnits(Set<HousingUnit> housingUnits) {
        this.housingUnits = housingUnits;
    }

    @Override
    public String toString() {
        return "HousingType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
