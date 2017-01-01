package animalkeeping.model;

/**
 * Created by jan on 28.12.16.
 */
public class TreatmentType {
    private Long id;
    private String name;
    private String description;
    private Boolean invasive;
    private License license;

    public TreatmentType() {}

    public TreatmentType(Long id, String name, Boolean invasive) {
        this.id = id;
        this.name = name;
        this.invasive = invasive;
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

    public Boolean isInvasive() {
        return invasive;
    }

    public void setInvasive(Boolean invasive) {
        this.invasive = invasive;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    @Override
    public String toString() {
        return "TreatmentType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", invasive=" + invasive +
                ", license=" + ((license != null) ? license.getName() : "") +
                '}';
    }
}
