package animalkeeping.model;

import animalkeeping.ui.Main;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by jan on 27.12.16.
 */
public class SpeciesType {
    private Long id;
    private String name;
    private String trivial;
    private Integer count;
    private Integer allTimeCount;

    public SpeciesType() {
    }

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getAllTimeCount() {
        return allTimeCount;
    }

    public void setAllTimeCount(Integer allTimeCount) {
        this.allTimeCount = allTimeCount;
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

