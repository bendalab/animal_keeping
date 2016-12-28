package model;

/**
 * Created by jan on 27.12.16.
 */

public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public Person() {
        // this form used by Hibernate
    }

    public Person(String first_name, String last_name, String email) {
        // for application use, to create new events
        this.firstName = first_name;
        this.lastName = last_name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}