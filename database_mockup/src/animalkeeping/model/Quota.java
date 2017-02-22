package animalkeeping.model;

/**
 * Created by jan on 28.12.16.
 */
public class Quota {
    private Long id;
    private Long number;
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

    public double getAvailableFraction() {
        if (getNumber() == null || getNumber() == 0 || getUsed() == null) {
            return 0.0;
        }
        return 1.0 - ((double)getUsed()/(double)getNumber());
    }

    @Override
    public String toString() {
        return "Quota{" +
                "number=" + number +
                ", speciesType=" + speciesType.getName() +
                ", license=" + license.getName() +
                '}';
    }
}
