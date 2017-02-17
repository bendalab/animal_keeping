package animalkeeping.technicalAdministration;

import java.util.List;

/**
 * Created by huben on 17.02.17.
 */
public class DatabaseUserType {
    private Long id;
    private String name;
    private List<String> privileges;

    DatabaseUserType(String name, List<String> privileges)
    {
        this.name = name;
        this.privileges = privileges;
            }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public List<String> getPrivileges(){
        return privileges;
    }

}
