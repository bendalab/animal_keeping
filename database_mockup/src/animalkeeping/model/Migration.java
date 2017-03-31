package animalkeeping.model;

import java.util.Date;

/**
 * Created by jan on 16.03.17.
 */
public class Migration {
    private long id;
    private String name;
    private java.util.Date migrationDate;

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

    public Date getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(Date migrationDate) {
        this.migrationDate = migrationDate;
    }
}
