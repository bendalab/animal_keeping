package animalkeeping.ui;

import animalkeeping.technicalAdministration.DatabaseUser;
import animalkeeping.technicalAdministration.DatabaseUserType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huben on 17.02.17.
 */
public class AddDatabaseUserForm extends VBox {
    TextField usernameField;
    PasswordField pwField;
    PasswordField pwConfirmField;
    private ComboBox<DatabaseUserType> userClassComboBox;

    public AddDatabaseUserForm(){
        init();
    }

    private void init() {
        userClassComboBox = new ComboBox<DatabaseUserType>();
        usernameField = new TextField();
        pwField = new PasswordField();
        pwConfirmField = new PasswordField();

        userClassComboBox.setConverter(new StringConverter<DatabaseUserType>() {
            @Override
            public String toString(DatabaseUserType object) {
                return object.getName();
            }

            @Override
            public DatabaseUserType fromString(String string) {
                return null;
            }
        });

        List<DatabaseUserType> userTypes = new ArrayList(0);
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            userTypes = session.createQuery("from DatabaseUserType", DatabaseUserType.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }

        userClassComboBox.getItems().addAll(userTypes);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        userClassComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        usernameField.prefWidthProperty().bind(column2.maxWidthProperty());
        pwField.prefWidthProperty().bind(column2.maxWidthProperty());
        pwConfirmField.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("User name:"), 0, 0);
        grid.add(usernameField, 1, 0, 1, 1);

        grid.add(new Label("Password:"), 0, 1);
        grid.add(pwField, 1, 1, 1, 1 );

        grid.add(new Label("Confirm password:"), 0, 2);
        grid.add(pwConfirmField, 1, 2, 1,1);

        grid.add(new Label("User type:"), 0,3);
        grid.add(userClassComboBox, 1,3, 1, 1);

        this.getChildren().add(grid);

    }



    public boolean addUser(){
        if (pwField.getText().equals(pwConfirmField.getText())) {
            DatabaseUser newUser = new DatabaseUser(usernameField.getText(), pwField.getText(), userClassComboBox.getValue());
            return true;
        }
        else{
            System.out.println("Passwords do not match!");
            return false;
        }


    }
}
