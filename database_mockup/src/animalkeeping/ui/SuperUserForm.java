package animalkeeping.ui;

import animalkeeping.technicalAdministration.DatabaseUserType;
import animalkeeping.util.AddDatabaseUserDialog;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

/**
 * Created by huben on 22.02.17.
 */
public class SuperUserForm extends VBox{
    TextField usernameField;
    PasswordField pwField;

    public SuperUserForm(){
        init();
    }
    private void init() {
        usernameField = new TextField();
        pwField = new PasswordField();


        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        usernameField.prefWidthProperty().bind(column2.maxWidthProperty());
        pwField.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("User name:"), 0, 0);
        grid.add(usernameField, 1, 0, 1, 1);

        grid.add(new Label("Password:"), 0, 1);
        grid.add(pwField, 1, 1, 1, 1 );

        this.getChildren().add(grid);

    }

    public Connection openDatabaseUserDialog(String rootName, String rootPassword) {
        String url = "jdbc:mysql://localhost:3306/animal_keeping";
    try {
        Connection connection = DriverManager.getConnection(url, rootName, rootPassword);

        return connection;
    }
    catch (SQLException e){
        e.printStackTrace();
        System.out.println("Access denied!");
        showInfo("Access denied!");
    }
    return null;

    }

    public String getSuperUserName() {
        return usernameField.getText();
    }
    public String getSuperUserPassword() {
        return pwField.getText();
    }

}
