/*package animalkeeping.technicalAdministration;

import org.hibernate.Session;

/**
 * Created by huben on 17.02.17.

public class DatabaseAdmin extends DatabaseUser {

   // DatabaseAdmin(String name, String password){
        //super(name, password);
        //give privileges
        //GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'localhost';

    }
    @Override
    public void giveRights(Session session){
        session.createQuery("GRANT ALL PRIVILEGES ON * . * TO '" + this.name + "'@'localhost';");
    }
}
*/