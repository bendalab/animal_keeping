package animalkeeping.model;

import animalkeeping.technicalAdministration.DatabaseUserType;

public class DatabaseUser {
    private long id;
    private String name;
    private Person person;
    private DatabaseUserType type;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public DatabaseUserType getType() {
        return type;
    }

    public void setType(DatabaseUserType type) {
        this.type = type;
    }
}
