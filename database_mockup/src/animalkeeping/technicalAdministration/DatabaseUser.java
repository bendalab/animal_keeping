package animalkeeping.technicalAdministration;

import animalkeeping.model.Subject;
import animalkeeping.ui.Main;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by huben on 17.02.17.
 */
public class DatabaseUser {
    Long id;
    String name;
    String password;


    public DatabaseUser(String name, String password, DatabaseUserType type){
        this.name = name;
        this.password = password;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.createQuery("CREATE USER " + name + "'@'localhost' IDENTIFIED BY " + password);
            session.getTransaction().commit();
            session.createQuery("GRANT" + type.getPrivileges() + " ON * . * TO '" + name + "'@'localhost';");
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        //CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'password';
    }

    public void giveRights(Session session){

    }

    public void addUser(){

    }

}
