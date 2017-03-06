package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import animalkeeping.ui.Main;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by jan on 27.12.16.
 */
public class SpeciesType extends Entity implements ChangeLogInterface {
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

    public String getType(){
        return this.getClass().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpeciesType))
            return false;
        if (obj == this)
            return true;

        SpeciesType rhs = (SpeciesType) obj;
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
        return "SpeciesType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", trivial='" + trivial + '\'' +
                '}';
    }
}

