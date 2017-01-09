package animalkeeping.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 27.12.16.
 */
public class Subject {
    private Long id;
    private String name;
    private String alias;
    private SpeciesType speciesType;
    private SubjectType subjectType;
    private SupplierType supplier;
    private Set<Treatment> treatments =
            new HashSet<Treatment>(0);
    public Subject() {}

    public Subject(String name, SpeciesType speciesType, SubjectType subjectType) {
        this.name = name;
        this.speciesType = speciesType;
        this.subjectType = subjectType;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public SpeciesType getSpeciesType() {
        return speciesType;
    }

    public void setSpeciesType(SpeciesType speciesType) {
        this.speciesType = speciesType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public SupplierType getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierType supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", speciesType=" + speciesType.getName() +
                ", subjectType=" + subjectType.getName() +
                ", supplier=" + supplier.getName() +
                '}';
    }
}
