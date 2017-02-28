package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jan on 27.12.16.
 */

public class Person extends Entity implements ChangeLogInterface {
    private String firstName;
    private String lastName;
    private String email;
    private String databaseUser;
    private Set<Treatment> treatments =
            new HashSet<>(0);

    public Person() {
        // this form used by Hibernate
    }

    public Person(String first_name, String last_name, String email) {
        // for application use, to create new events
        this.firstName = first_name;
        this.lastName = last_name;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDatabaseUser() {return databaseUser;}

    public void setDatabaseUser(String userName) { this.databaseUser = userName;}

    public Set<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<Treatment> treatments) {
        this.treatments = treatments;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person))
            return false;
        if (obj == this)
            return true;

        Person rhs = (Person) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(getFirstName(), rhs.getFirstName()).
                        append(getLastName(), rhs.getLastName()).
                        append(getId(), rhs.getId()).
                        isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                        append(getId()).
                        append(getFirstName()).
                        append(getLastName()).
                        toHashCode();
    }

    @Override
    public String getType(){
        return this.getClass().toString();
    }
}