package animalkeeping.technicalAdministration;

import animalkeeping.model.Subject;
import animalkeeping.ui.Main;
import animalkeeping.ui.SuperUserForm;
import animalkeeping.util.SuperUserDialog;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

/**
 * Created by huben on 17.02.17.
 */
public class DatabaseUser {
    Long id;
    String name;
    String password;


    public DatabaseUser(String name, String password, DatabaseUserType type, Connection connection){
        this.name = name;
        this.password = password;

        SuperUserDialog superDialog = new SuperUserDialog();



        try {
            Statement stmt = connection.createStatement();
            String createUser = "CREATE USER " + name + "@localhost IDENTIFIED BY \"" + password + "\"";
            String grantPrivilege = "GRANT " + type.getPrivileges() + " ON * . * TO '" + name + "'@'localhost'";
            stmt.executeUpdate(createUser);
            stmt.executeUpdate(grantPrivilege);
            showInfo("Successfully added user to database!");

        }
        catch (SQLException e) {
            showInfo(e.getMessage());
            System.out.println( e.getMessage() );
        }
            Session session = Main.sessionFactory.openSession();

        //CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'password';
    }

    public void giveRights(Session session){

    }

    public void addUser(){

    }

}
