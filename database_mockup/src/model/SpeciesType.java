package model;

/**
 * Created by jan on 27.12.16.
 */
public class SpeciesType {
    private Long id;
    private String name;
    private String trivial;

    public SpeciesType() {}

    public SpeciesType(String name, String trivial) {
        this.name = name;
        this.trivial = trivial;
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

    public String getTrivial() {
        return trivial;
    }

    public void setTrivial(String trivial) {
        this.trivial = trivial;
    }

    @Override
    public String toString() {
        return "SpeciesType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", trivial='" + trivial + '\'' +
                '}';
    }
}

