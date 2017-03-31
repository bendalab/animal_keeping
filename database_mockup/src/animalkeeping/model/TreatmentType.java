package animalkeeping.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 28.12.16.
 */
public class TreatmentType {
    private Long id;
    private String name;
    private String description;
    private Boolean invasive;
    private Boolean finalExperiment;
    private TreatmentTarget target;
    private License license;
    private Set<Treatment> treatments =
            new HashSet<>(0);


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

    public Boolean isFinalExperiment() {
        return finalExperiment;
    }

    public void setFinalExperiment(Boolean finalExperiment) {
        this.finalExperiment = finalExperiment;
    }

    public TreatmentTarget getTarget() {
        return target;
    }

    public void setTarget(TreatmentTarget target) {
        this.target = target;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Set<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<Treatment> treatments) {
        this.treatments = treatments;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TreatmentType))
            return false;
        if (obj == this)
            return true;

        TreatmentType rhs = (TreatmentType) obj;
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
        return "TreatmentType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", invasive=" + invasive +
                ", is final=" + finalExperiment +
                ", license=" + ((license != null) ? license.getName() : "") +
                '}';
    }
}
